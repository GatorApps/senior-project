import { useState, useEffect, useRef } from "react"
import { axiosPrivate } from "../../apis/backend"
import { useSelector } from "react-redux"
import { useLocation, useNavigate } from "react-router-dom"
import HelmetComponent from "../../components/HelmetComponent/HelmetComponent"
import Header from "../../components/Header/Header"
import Footer from "../../components/Footer/Footer"
import Box from "@mui/material/Box"
import Button from "@mui/material/Button"
import Container from "@mui/material/Container"
import Paper from "@mui/material/Paper"
import Typography from "@mui/material/Typography"
import {
  FormControl,
  MenuItem,
  Select,
  TextField,
  CircularProgress,
  Alert,
  Snackbar,
  FormLabel,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material"
import ReactQuill from "react-quill"
import "react-quill/dist/quill.snow.css"
import CreateLabPopup from "../../components/CreateLabPopup/CreateLabPopup"

const PostingEditor = () => {
  const userInfo = useSelector((state) => state.auth.userInfo)
  const navigate = useNavigate()
  const location = useLocation()
  const searchParams = new URLSearchParams(location.search)
  const postingId = searchParams.get("postingId")

  // Default value for supplemental questions
  const DEFAULT_SUPPLEMENTAL_QUESTIONS =
    "<p><strong><em>(We provided some supplemental questions bellow that we found commonly helpful. Feel free to adjust them based on your needs. Please delete this info line when you're done editing.)</em></strong></p><p><br></p><p>How many hours can you dedicate per week to this responsibility?</p><p><br></p><p><br></p><p>Please list your other commitments this semester:</p><ul><li>Commitment 1</li><li>Commitment 2</li><li>Commitment 3</li></ul><p><br></p><p>Why are you specifically interested in this position?</p><p><br></p><p><br></p>"

  // State variables
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [successMessage, setSuccessMessage] = useState("")
  const [formSubmitting, setFormSubmitting] = useState(false)
  const [showConfirmation, setShowConfirmation] = useState(false)

  // Form fields
  const [id, setId] = useState("")
  const [labId, setLabId] = useState("")
  const [name, setName] = useState("")
  const [rawDescription, setRawDescription] = useState("")
  const [supplementalQuestions, setSupplementalQuestions] = useState("")
  const [status, setStatus] = useState("open") // Default to open
  const [labs, setLabs] = useState([])
  const [formErrors, setFormErrors] = useState({})
  const [noLabsExist, setNoLabsExist] = useState(false)
  const [createLabOpen, setCreateLabOpen] = useState(false)

  // Use a ref to track if the form has been modified
  const formModified = useRef(false)

  // Store initial form values
  const initialValues = useRef({
    labId: "",
    name: "",
    rawDescription: "",
    supplementalQuestions: "",
  })

  // Determine if we're editing or creating
  const isEditing = Boolean(postingId)
  const pageTitle = isEditing ? "Edit Posting" : "New Posting"

  // Initialize form for new posting
  useEffect(() => {
    if (!isEditing) {
      // Set default value for supplemental questions for new postings
      setSupplementalQuestions(DEFAULT_SUPPLEMENTAL_QUESTIONS)

      // Update initial values to include the default
      initialValues.current = {
        ...initialValues.current,
        supplementalQuestions: DEFAULT_SUPPLEMENTAL_QUESTIONS,
      }
    }
  }, [isEditing])

  // Check if form has actually changed
  const hasFormChanged = () => {
    return (
      formModified.current &&
      (labId !== initialValues.current.labId ||
        name !== initialValues.current.name ||
        rawDescription !== initialValues.current.rawDescription ||
        supplementalQuestions !== initialValues.current.supplementalQuestions)
    )
  }

  // Set up browser confirmation for page unload
  useEffect(() => {
    const handleBeforeUnload = (e) => {
      if (hasFormChanged()) {
        // Standard for most browsers
        e.preventDefault()
        // Required for some older browsers
        const message = "You have unsaved changes. Are you sure you want to leave?"
        e.returnValue = message
        return message
      }
    }

    window.addEventListener("beforeunload", handleBeforeUnload)

    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload)
    }
  }, [labId, name, rawDescription, supplementalQuestions])

  // Validation function
  const validateForm = () => {
    const errors = {}

    if (!labId) errors.labId = "Please select a lab"
    if (!name.trim()) errors.name = "Title is required"
    if (!rawDescription.trim() || rawDescription === "<p><br></p>") errors.description = "Description is required"

    setFormErrors(errors)
    return Object.keys(errors).length === 0
  }

  const handleSave = async () => {
    if (!validateForm()) {
      setError("Please fill in all required fields")
      return
    }

    try {
      setFormSubmitting(true)
      setError(null)

      const payload = {
        id,
        labId,
        name,
        description: rawDescription,
        supplementalQuestions: supplementalQuestions || DEFAULT_SUPPLEMENTAL_QUESTIONS,
        status: "open", // Always set status to "open"
      }

      if (isEditing) {
        await axiosPrivate.put("/posting/postingEditor", payload)
        setSuccessMessage("Posting updated successfully")
      } else {
        delete payload.id
        await axiosPrivate.post("/posting/postingEditor", payload)
        setSuccessMessage("Posting created successfully")
      }

      // Update initial values and reset modified flag
      initialValues.current = {
        labId,
        name,
        rawDescription,
        supplementalQuestions,
      }
      formModified.current = false

      // Navigate after a short delay to allow the user to see the success message
      setTimeout(() => {
        navigate("/postingManagement")
      }, 1500)
    } catch (err) {
      setError(err.response?.data?.message || err.message || "An error occurred while saving")
    } finally {
      setFormSubmitting(false)
    }
  }

  const fetchExistingPosting = async () => {
    try {
      setLoading(true)
      const response = await axiosPrivate.get(`/posting/postingEditor?positionId=${postingId}`)

      if (response.data?.payload?.position) {
        const position = response.data.payload.position
        const posLabId = position.labId || ""
        const posName = position.name || ""
        const posDescription = position.description || ""
        const posSupplementalQuestions = position.supplementalQuestions || ""

        // Set form values
        setId(position.id || "")
        setLabId(posLabId)
        setName(posName)
        setRawDescription(posDescription)
        setSupplementalQuestions(posSupplementalQuestions)
        setStatus(position.status || "open")

        // Set initial values
        initialValues.current = {
          labId: posLabId,
          name: posName,
          rawDescription: posDescription,
          supplementalQuestions: posSupplementalQuestions,
        }

        // Reset modified flag since we just loaded the data
        formModified.current = false
      } else {
        setError("Posting not found")
      }
    } catch (error) {
      setError(error.response?.data?.message || error.message || "Error fetching posting details")
    } finally {
      setLoading(false)
    }
  }

  const fetchLabs = async () => {
    try {
      setLoading(true)
      setError(null) // Clear any previous errors

      const response = await axiosPrivate.get("/lab/labsList")

      if (response.data?.payload?.labs) {
        const labsList = response.data.payload.labs
        setLabs(labsList)

        // Check if there are no labs
        if (labsList.length === 0) {
          setNoLabsExist(true)
          // If editing a posting but the lab no longer exists, show an error
          if (isEditing) {
            setError("The lab associated with this posting no longer exists. Please create a new lab first.")
          }
          // For new postings, we'll show a message in the UI
        } else {
          setNoLabsExist(false)

          // If no lab is selected and we have labs, select the first one by default for new postings
          if (!labId && labsList.length > 0 && !isEditing) {
            const firstLabId = labsList[0].labId
            setLabId(firstLabId)

            // Update initial value for labId
            initialValues.current = {
              ...initialValues.current,
              labId: firstLabId,
            }
          }
        }
      } else {
        setLabs([])
        setNoLabsExist(true)
      }
    } catch (error) {
      console.error("Error fetching labs:", error)
      // More specific error handling
      if (error.response?.status === 404) {
        setError("Unable to retrieve labs. The lab list service is currently unavailable.")
      } else {
        setError(error.response?.data?.message || error.message || "Error fetching labs")
      }
      setNoLabsExist(true) // Assume no labs on error to be safe
    } finally {
      setLoading(false)
    }
  }

  // Refresh labs after creating a new one
  const handleLabPopupClose = () => {
    setCreateLabOpen(false)

    // Add a small delay before fetching labs to ensure the backend has time to process
    // the lab creation and make it available in the labs list
    setTimeout(() => {
      fetchLabs()
    }, 500)
  }

  const handleCancel = () => {
    if (hasFormChanged()) {
      setShowConfirmation(true)
    } else {
      navigate("/postingManagement")
    }
  }

  const handleInputChange = (field, value) => {
    // Mark form as modified
    formModified.current = true

    if (field === "labId") {
      setLabId(value)
      setFormErrors({ ...formErrors, labId: null })
    } else if (field === "name") {
      setName(value)
      setFormErrors({ ...formErrors, name: null })
    } else if (field === "description") {
      setRawDescription(value)
      setFormErrors({ ...formErrors, description: null })
    } else if (field === "supplementalQuestions") {
      setSupplementalQuestions(value)
    }
  }

  useEffect(() => {
    if (isEditing) {
      fetchExistingPosting()
    } else {
      // For new postings, initialize with default values
      setSupplementalQuestions(DEFAULT_SUPPLEMENTAL_QUESTIONS)

      initialValues.current = {
        labId: "",
        name: "",
        rawDescription: "",
        supplementalQuestions: DEFAULT_SUPPLEMENTAL_QUESTIONS,
      }
      formModified.current = false
    }
    fetchLabs()
  }, [isEditing])

  // Removed the auto-open useEffect for the create lab popup

  return (
    <HelmetComponent title={pageTitle}>
      <div className="GenericPage">
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">{pageTitle}</Typography>
                </Box>
                <Box
                  className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_right"
                  sx={{ flexGrow: "1" }}
                >
                  <Box className="GenericPage__container_title_flexBox_right"></Box>
                </Box>
              </Box>
            </Container>
            <Container maxWidth="lg">
              <Paper variant="outlined" sx={{ padding: 3 }}>
                {loading ? (
                  <Box sx={{ display: "flex", justifyContent: "center", padding: 4 }}>
                    <CircularProgress />
                  </Box>
                ) : noLabsExist ? (
                  <Box sx={{ textAlign: "center", py: 4 }}>
                    <Alert severity="info" sx={{ mb: 3 }}>
                      You do not currently manage any labs
                    </Alert>
                    <Typography variant="body1" sx={{ mt: 8, mb: 4 }}>
                      Please create a lab profile to associate with your posting, or ask your lab profile creator to add you as a manager.
                    </Typography>
                    <Box sx={{ display: "flex", justifyContent: "center", gap: 2 }}>
                      <Button variant="outlined" color="error" onClick={() => navigate("/postingManagement")}>
                        Cancel
                      </Button>
                      <Button variant="contained" color="primary" onClick={() => setCreateLabOpen(true)}>
                        Create Lab
                      </Button>
                    </Box>
                  </Box>
                ) : (
                  <Box>
                    {error && (
                      <Alert severity="error" sx={{ marginBottom: 3 }}>
                        {error}
                      </Alert>
                    )}

                    <FormControl fullWidth sx={{ marginBottom: 2 }}>
                      <FormLabel
                        component="legend"
                        sx={{
                          fontSize: "0.875rem",
                          fontWeight: 500,
                          marginBottom: 1,
                          color: "text.primary",
                        }}
                      >
                        Lab <span style={{ color: "red" }}>*</span>
                      </FormLabel>
                      <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                        <Select
                          value={labId}
                          onChange={(e) => handleInputChange("labId", e.target.value)}
                          error={Boolean(formErrors.labId)}
                          size="small"
                          sx={{ flexGrow: 1 }}
                        >
                          {labs.map((lab) => (
                            <MenuItem key={lab.labId} value={lab.labId}>
                              {lab.labName}
                            </MenuItem>
                          ))}
                        </Select>
                        <Button
                          variant="outlined"
                          size="small"
                          onClick={() => setCreateLabOpen(true)}
                          sx={{ whiteSpace: "nowrap" }}
                        >
                          Manage Labs
                        </Button>
                      </Box>
                      {formErrors.labId && (
                        <Typography color="error" variant="caption">
                          {formErrors.labId}
                        </Typography>
                      )}
                      <Typography
                        variant="caption"
                        color="text.secondary"
                        sx={{
                          mt: 0.5,
                          fontStyle: "italic",
                          display: "block",
                        }}
                      >
                        Don't see your lab? Use the Manage Labs button to create or edit labs.
                      </Typography>
                    </FormControl>

                    <FormLabel
                      component="legend"
                      sx={{
                        fontSize: "0.875rem",
                        fontWeight: 500,
                        marginBottom: 1,
                        color: "text.primary",
                      }}
                    >
                      Title <span style={{ color: "red" }}>*</span>
                    </FormLabel>
                    <TextField
                      fullWidth
                      value={name}
                      onChange={(e) => handleInputChange("name", e.target.value)}
                      placeholder="Enter position title"
                      error={Boolean(formErrors.name)}
                      helperText={formErrors.name}
                      size="small"
                      sx={{ marginBottom: 3 }}
                    />

                    <FormLabel
                      component="legend"
                      sx={{
                        fontSize: "0.875rem",
                        fontWeight: 500,
                        marginBottom: 1,
                        color: "text.primary",
                      }}
                    >
                      Description <span style={{ color: "red" }}>*</span>
                    </FormLabel>
                    <Box
                      sx={{
                        marginBottom: 3,
                        ".ql-editor": {
                          minHeight: "150px",
                          maxHeight: "400px",
                          overflow: "auto",
                        },
                        border: formErrors.description ? "1px solid #d32f2f" : "none",
                      }}
                    >
                      <ReactQuill
                        value={rawDescription}
                        onChange={(value) => handleInputChange("description", value)}
                        placeholder="Enter position description"
                      />
                      {formErrors.description && (
                        <Typography color="error" variant="caption">
                          {formErrors.description}
                        </Typography>
                      )}
                    </Box>

                    <FormLabel
                      component="legend"
                      sx={{
                        fontSize: "0.875rem",
                        fontWeight: 500,
                        marginBottom: 1,
                        color: "text.primary",
                      }}
                    >
                      Supplemental Questions
                    </FormLabel>
                    <Box
                      sx={{
                        marginBottom: 4,
                        ".ql-editor": {
                          minHeight: "100px",
                          maxHeight: "300px",
                          overflow: "auto",
                        },
                      }}
                    >
                      <ReactQuill
                        value={supplementalQuestions}
                        onChange={(value) => handleInputChange("supplementalQuestions", value)}
                        placeholder="Enter any additional questions for applicants (optional)"
                      />
                    </Box>

                    <Box sx={{ display: "flex", justifyContent: "flex-end", alignItems: "center", marginTop: 2 }}>
                      <Button
                        variant="outlined"
                        onClick={handleCancel}
                        color="error"
                        disabled={formSubmitting}
                        sx={{ marginRight: 2, paddingX: 3, paddingY: 0.75 }}
                      >
                        Cancel
                      </Button>

                      <Button
                        variant="contained"
                        onClick={handleSave}
                        color="primary"
                        disabled={formSubmitting}
                        sx={{ paddingX: 3, paddingY: 0.75 }}
                      >
                        {formSubmitting ? (
                          <CircularProgress size={24} color="inherit" />
                        ) : isEditing ? (
                          "Update"
                        ) : (
                          "Create"
                        )}
                      </Button>
                    </Box>
                  </Box>
                )}
              </Paper>

              {/* CreateLabPopup should only ask for lab name, website, and description */}
              <CreateLabPopup open={createLabOpen} onClose={handleLabPopupClose} labs={labs} />

              <Snackbar
                open={Boolean(successMessage)}
                autoHideDuration={3000}
                onClose={() => setSuccessMessage("")}
                anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
              >
                <Alert onClose={() => setSuccessMessage("")} severity="success" sx={{ width: "100%" }}>
                  {successMessage}
                </Alert>
              </Snackbar>

              {/* Confirmation Dialog */}
              <Dialog
                open={showConfirmation}
                onClose={() => setShowConfirmation(false)}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
              >
                <DialogTitle id="alert-dialog-title">{"Discard changes?"}</DialogTitle>
                <DialogContent>
                  <DialogContentText id="alert-dialog-description">
                    You have unsaved changes. Are you sure you want to discard them?
                  </DialogContentText>
                </DialogContent>
                <DialogActions>
                  <Button onClick={() => setShowConfirmation(false)} color="primary">
                    Continue Editing
                  </Button>
                  <Button onClick={() => navigate("/postingManagement")} color="error" autoFocus>
                    Discard Changes
                  </Button>
                </DialogActions>
              </Dialog>
            </Container>
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent>
  )
}

export default PostingEditor
