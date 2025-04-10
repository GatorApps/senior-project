import { useState, useEffect } from "react"
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
import { FormControl, MenuItem, Select, TextField, CircularProgress, Alert, Snackbar, FormLabel } from "@mui/material"
import ReactQuill from "react-quill"
import "react-quill/dist/quill.snow.css"
import CreateLabPopup from "../../components/CreateLabPopup/CreateLabPopup"

const PostingEditor = () => {
  const userInfo = useSelector((state) => state.auth.userInfo)
  const navigate = useNavigate()
  const location = useLocation()
  const searchParams = new URLSearchParams(location.search)
  const postingId = searchParams.get("postingId")

  // State variables
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [successMessage, setSuccessMessage] = useState("")
  const [formSubmitting, setFormSubmitting] = useState(false)

  // Form fields
  const [id, setId] = useState("")
  const [labId, setLabId] = useState("")
  const [name, setName] = useState("")
  const [rawDescription, setRawDescription] = useState("")
  const [supplementalQuestions, setSupplementalQuestions] = useState("")
  const [status, setStatus] = useState("open") // Default to open
  const [labs, setLabs] = useState([])
  const [formErrors, setFormErrors] = useState({})

  // Create lab popup state
  const [open, setOpen] = useState(false)

  // Determine if we're editing or creating
  const isEditing = Boolean(postingId)
  const pageTitle = isEditing ? "Edit Posting" : "New Posting"

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
        supplementalQuestions: supplementalQuestions || "",
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
        setId(position.id || "")
        setLabId(position.labId || "")
        setName(position.name || "")
        setRawDescription(position.description || "")
        setSupplementalQuestions(position.supplementalQuestions || "")
        setStatus(position.status || "open")
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
      const response = await axiosPrivate.get("/lab/labsList")

      if (response.data?.payload?.labs) {
        setLabs(response.data.payload.labs)

        // If no lab is selected and we have labs, select the first one by default
        if (!labId && response.data.payload.labs.length > 0 && !isEditing) {
          setLabId(response.data.payload.labs[0].labId)
        }
      } else {
        setLabs([])
      }
    } catch (error) {
      setError(error.response?.data?.message || error.message || "Error fetching labs")
    } finally {
      setLoading(false)
    }
  }

  // Refresh labs after creating a new one
  const handleLabPopupClose = () => {
    setOpen(false)
    fetchLabs()
  }

  useEffect(() => {
    if (isEditing) {
      fetchExistingPosting()
    }
    fetchLabs()
  }, [isEditing])

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
                ) : (
                  <Box>
                    {error && (
                      <Alert severity="error" sx={{ marginBottom: 3 }}>
                        {error}
                      </Alert>
                    )}

                    <FormControl fullWidth sx={{ marginBottom: 3 }}>
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
                      <Select
                        value={labId}
                        onChange={(e) => {
                          setLabId(e.target.value)
                          setFormErrors({ ...formErrors, labId: null })
                        }}
                        error={Boolean(formErrors.labId)}
                        displayEmpty
                        size="small"
                      >
                        <MenuItem value="" disabled>
                          Select a lab
                        </MenuItem>
                        {labs.length > 0 ? (
                          labs.map((lab) => (
                            <MenuItem key={lab.labId} value={lab.labId}>
                              {lab.labName}
                            </MenuItem>
                          ))
                        ) : (
                          <MenuItem value="" disabled>
                            No labs found
                          </MenuItem>
                        )}
                      </Select>
                      {formErrors.labId && (
                        <Typography color="error" variant="caption">
                          {formErrors.labId}
                        </Typography>
                      )}
                    </FormControl>

                    <Box sx={{ display: "flex", justifyContent: "end", alignItems: "center", marginBottom: 3 }}>
                      <Typography variant="body2" color="text.secondary" sx={{ marginRight: 2 }}>
                        Don't see your lab? <strong>Create or edit existing</strong>
                      </Typography>
                      <Button variant="outlined" size="small" onClick={() => setOpen(true)}>
                        Manage Labs
                      </Button>
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
                      Title <span style={{ color: "red" }}>*</span>
                    </FormLabel>
                    <TextField
                      fullWidth
                      value={name}
                      onChange={(e) => {
                        setName(e.target.value)
                        setFormErrors({ ...formErrors, name: null })
                      }}
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
                        onChange={(value) => {
                          setRawDescription(value)
                          setFormErrors({ ...formErrors, description: null })
                        }}
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
                        onChange={(value) => setSupplementalQuestions(value)}
                        placeholder="Enter any additional questions for applicants (optional)"
                      />
                    </Box>

                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 2 }}>
                      <Button
                        variant="outlined"
                        onClick={() => navigate("/postingManagement")}
                        color="error"
                        disabled={formSubmitting}
                        sx={{ paddingX: 3, paddingY: 0.75 }}
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
              <CreateLabPopup open={open} onClose={handleLabPopupClose} labs={labs} />

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
            </Container>
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent>
  )
}

export default PostingEditor
