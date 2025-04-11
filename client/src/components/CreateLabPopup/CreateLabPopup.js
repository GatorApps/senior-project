import { useState, useEffect } from "react"
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  FormControl,
  MenuItem,
  Select,
  Typography,
  InputLabel,
  Box,
  Alert,
  CircularProgress,
  FormLabel,
  DialogContentText,
} from "@mui/material"
import { axiosPrivate } from "../../apis/backend"
import ReactQuill from "react-quill"
import "react-quill/dist/quill.snow.css"

const CreateLabPopup = ({ open, onClose, labs }) => {
  // Form fields
  const [name, setName] = useState("")
  const [website, setWebsite] = useState("")
  const [description, setDescription] = useState("")
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [labId, setLabId] = useState("New Lab")
  const [formErrors, setFormErrors] = useState({})
  const [showConfirmation, setShowConfirmation] = useState(false)
  const [formChanged, setFormChanged] = useState(false)

  // Keep track of original values for existing labs
  const [originalValues, setOriginalValues] = useState({
    name: "",
    website: "",
    description: "",
  })

  // Reset form to default values
  const resetForm = () => {
    setName("")
    setWebsite("")
    setDescription("")
    setLabId("New Lab")
    setFormErrors({})
    setFormChanged(false)
    setError(null)
    setOriginalValues({
      name: "",
      website: "",
      description: "",
    })
  }

  // Effect to reset form when dialog opens/closes
  useEffect(() => {
    if (!open) {
      // Reset form when dialog closes
      resetForm()
    }
  }, [open])

  const fetchExistingLab = async (labId) => {
    try {
      setLoading(true)
      setError(null)
      setFormErrors({})

      const response = await axiosPrivate.get(`/lab/profileEditor?labId=${labId}`)
      if (response.data.errCode === "0") {
        const lab = response.data.payload.lab
        const labName = lab.name || ""
        const labWebsite = lab.website || ""
        const labDescription = lab.description || ""

        // Set form values
        setName(labName)
        setWebsite(labWebsite)
        setDescription(labDescription)

        // Store original values
        setOriginalValues({
          name: labName,
          website: labWebsite,
          description: labDescription,
        })

        // Reset form changed flag since we just loaded data
        setFormChanged(false)
      } else {
        setError(response.data.errMsg || "Error fetching lab profile")
      }
    } catch (err) {
      setError(err.message || "Error fetching lab profile")
    } finally {
      setLoading(false)
    }
  }

  const handleLabChange = (e) => {
    const newLabId = e.target.value

    // Check if there are unsaved changes before switching labs
    if (formChanged) {
      setShowConfirmation(true)
      return
    }

    setLabId(newLabId)
    setFormErrors({})
    setError(null)

    if (newLabId !== "New Lab") {
      fetchExistingLab(newLabId)
    } else {
      // Reset form for new lab
      setName("")
      setWebsite("")
      setDescription("")
      setFormChanged(false)
      setOriginalValues({
        name: "",
        website: "",
        description: "",
      })
    }
  }

  const validateForm = () => {
    const errors = {}

    if (!name.trim()) errors.name = "Lab name is required"
    if (!website.trim()) errors.website = "Website URL is required"
    if (!description.trim() || description === "<p><br></p>") errors.description = "Description is required"

    setFormErrors(errors)
    return Object.keys(errors).length === 0
  }

  const handleSubmit = async () => {
    if (!validateForm()) {
      setError("Please fill in all required fields")
      return
    }

    setLoading(true)
    setError(null)
    setFormErrors({})

    const labData = {
      name,
      website,
      description,
    }

    // If editing an existing lab, include the labId in the body
    if (labId !== "New Lab") {
      labData.id = labId
    }

    try {
      let response

      if (labId === "New Lab") {
        // Create a new lab with a POST request
        response = await axiosPrivate.post("/lab/profileEditor", labData)
      } else {
        // Update an existing lab with a PUT request
        response = await axiosPrivate.put("/lab/profileEditor", labData)
      }

      if (response.data.errCode === "0") {
        setFormChanged(false)
        onClose() // Close modal after successful submission
      } else {
        // Handle validation errors from the API
        if (response.data.errCode === "ERR_INPUT_FAIL_VALIDATION" && response.data.payload) {
          // Set specific field errors from the API response
          const apiErrors = {}

          // Process each field error from the payload
          Object.entries(response.data.payload).forEach(([field, message]) => {
            apiErrors[field] = message
          })

          setFormErrors(apiErrors)
          setError(response.data.errMsg || "Please fix the validation errors and try again")
        } else {
          // Set generic error message
          setError(response.data.errMsg || "Error processing lab profile")
        }
      }
    } catch (err) {
      // Handle error response with validation details
      if (err.response?.data?.errCode === "ERR_INPUT_FAIL_VALIDATION" && err.response.data.payload) {
        const apiErrors = {}

        // Process each field error from the payload
        Object.entries(err.response.data.payload).forEach(([field, message]) => {
          apiErrors[field] = message
        })

        setFormErrors(apiErrors)
        setError(err.response.data.errMsg || "Please fix the validation errors and try again")
      } else {
        // Set generic error message
        setError(err.response?.data?.errMsg || err.message || "Error processing lab profile")
      }
    } finally {
      setLoading(false)
    }
  }

  const handleClose = () => {
    if (formChanged) {
      setShowConfirmation(true)
    } else {
      resetAndClose()
    }
  }

  const resetAndClose = () => {
    // Reset form state when closing
    resetForm()
    setShowConfirmation(false)
    onClose()
  }

  const continueEditing = () => {
    setShowConfirmation(false)
  }

  const handleInputChange = (field, value) => {
    setFormChanged(true)

    if (field === "name") {
      setName(value)
      setFormErrors({ ...formErrors, name: null })
    } else if (field === "website") {
      setWebsite(value)
      setFormErrors({ ...formErrors, website: null })
    } else if (field === "description") {
      setDescription(value)
      setFormErrors({ ...formErrors, description: null })
    }
  }

  return (
    <>
      <Dialog
        open={open}
        onClose={handleClose}
        maxWidth="sm"
        fullWidth
        PaperProps={{
          sx: {
            borderRadius: 1,
          },
        }}
      >
        <DialogTitle
          sx={{
            fontSize: "1.25rem",
            fontWeight: 500,
            paddingBottom: 1,
            borderBottom: "1px solid",
            borderColor: "divider",
          }}
        >
          {labId === "New Lab" ? "Create New Lab" : "Edit Lab Profile"}
        </DialogTitle>

        <DialogContent sx={{ paddingTop: 4, paddingBottom: 3 }}>
          {error && (
            <Alert severity="error" sx={{ marginBottom: 2 }}>
              {error}
            </Alert>
          )}

          {loading && (
            <Box sx={{ display: "flex", justifyContent: "center", padding: 2 }}>
              <CircularProgress size={30} />
            </Box>
          )}

          <FormControl fullWidth sx={{ marginY: 3 }}>
            <InputLabel id="lab-select-label" size="small">
              Select Lab
            </InputLabel>
            <Select
              label="Select Lab"
              value={labId || ""}
              onChange={handleLabChange}
              labelId="lab-select-label"
              size="small"
            >
              <MenuItem value="New Lab">New Lab</MenuItem>
              {labs.length > 0 &&
                labs.map((lab) => (
                  <MenuItem key={lab.labId} value={lab.labId}>
                    {lab.labName}
                  </MenuItem>
                ))}
            </Select>
          </FormControl>

          <FormLabel
            component="legend"
            sx={{
              fontSize: "0.875rem",
              fontWeight: 500,
              marginBottom: 0.5,
              color: "text.primary",
            }}
          >
            Lab Name <span style={{ color: "red" }}>*</span>
          </FormLabel>
          <TextField
            fullWidth
            value={name || ""}
            onChange={(e) => handleInputChange("name", e.target.value)}
            error={Boolean(formErrors.name)}
            helperText={formErrors.name}
            placeholder="Enter lab name"
            size="small"
            sx={{ marginBottom: 2 }}
          />

          <FormLabel
            component="legend"
            sx={{
              fontSize: "0.875rem",
              fontWeight: 500,
              marginBottom: 0.5,
              color: "text.primary",
            }}
          >
            Lab Website <span style={{ color: "red" }}>*</span>
          </FormLabel>
          <TextField
            fullWidth
            value={website || ""}
            onChange={(e) => handleInputChange("website", e.target.value)}
            error={Boolean(formErrors.website)}
            helperText={formErrors.website}
            placeholder="https://example-lab.ufl.edu"
            size="small"
            sx={{ marginBottom: 2 }}
          />

          <FormLabel
            component="legend"
            sx={{
              fontSize: "0.875rem",
              fontWeight: 500,
              marginBottom: 0.5,
              color: "text.primary",
            }}
          >
            Description <span style={{ color: "red" }}>*</span>
          </FormLabel>
          <Box
            sx={{
              ".ql-editor": {
                minHeight: "120px",
                maxHeight: "250px",
                overflow: "auto",
              },
              border: formErrors.description ? "1px solid #d32f2f" : "none",
              marginBottom: 1,
            }}
          >
            <ReactQuill
              theme="snow"
              value={description || ""}
              onChange={(value) => handleInputChange("description", value)}
              placeholder="Enter lab description"
            />
            {formErrors.description && (
              <Typography color="error" variant="caption" sx={{ marginTop: 0.5, display: "block" }}>
                {formErrors.description}
              </Typography>
            )}
          </Box>
        </DialogContent>

        <DialogActions
          sx={{
            padding: 2,
            borderTop: "1px solid",
            borderColor: "divider",
            justifyContent: "flex-end",
          }}
        >
          <Button
            onClick={handleClose}
            color="error"
            variant="outlined"
            disabled={loading}
            size="small"
            sx={{ marginRight: 1 }}
          >
            Cancel
          </Button>
          <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading} size="small">
            {loading ? <CircularProgress size={20} color="inherit" /> : "Save"}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Confirmation Dialog */}
      <Dialog
        open={showConfirmation}
        onClose={continueEditing}
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
          <Button onClick={continueEditing} color="primary">
            Continue Editing
          </Button>
          <Button onClick={resetAndClose} color="error" autoFocus>
            Discard Changes
          </Button>
        </DialogActions>
      </Dialog>
    </>
  )
}

export default CreateLabPopup
