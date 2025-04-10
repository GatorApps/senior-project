import { useState } from "react"
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
} from "@mui/material"
import { axiosPrivate } from "../../apis/backend"
import ReactQuill from "react-quill"
import "react-quill/dist/quill.snow.css"

const CreateLabPopup = ({ open, onClose, labs }) => {
  const [name, setName] = useState("")
  const [website, setWebsite] = useState("")
  const [description, setDescription] = useState("")
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [labId, setLabId] = useState("New Lab")
  const [formErrors, setFormErrors] = useState({})

  const fetchExistingLab = async (labId) => {
    try {
      setLoading(true)
      const response = await axiosPrivate.get(`/lab/profileEditor?labId=${labId}`)
      if (response.data.errCode === "0") {
        const lab = response.data.payload.lab
        setName(lab.name || "")
        setWebsite(lab.website || "")
        setDescription(lab.description || "")
      } else {
        setError(response.data.message || "Error fetching lab profile")
      }
    } catch (err) {
      setError(err.message || "Error fetching lab profile")
    } finally {
      setLoading(false)
    }
  }

  const handleLabChange = (e) => {
    setLabId(e.target.value)
    setFormErrors({})

    if (e.target.value !== "New Lab") {
      fetchExistingLab(e.target.value)
    } else {
      // Reset form fields when creating a new lab
      setName("")
      setWebsite("")
      setDescription("")
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
        onClose() // Close modal after successful submission
      } else {
        setError(response.data.message || "Error processing lab profile")
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Error processing lab profile")
    } finally {
      setLoading(false)
    }
  }

  const handleClose = () => {
    // Reset form state when closing
    setFormErrors({})
    setError(null)
    onClose()
  }

  return (
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

      <DialogContent sx={{ paddingTop: 3 }}>
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

        <FormControl fullWidth sx={{ marginBottom: 3 }}>
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
          onChange={(e) => {
            setName(e.target.value)
            setFormErrors({ ...formErrors, name: null })
          }}
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
          onChange={(e) => {
            setWebsite(e.target.value)
            setFormErrors({ ...formErrors, website: null })
          }}
          error={Boolean(formErrors.website)}
          helperText={formErrors.website}
          placeholder="https://example.com"
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
            onChange={(value) => {
              setDescription(value)
              setFormErrors({ ...formErrors, description: null })
            }}
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
          justifyContent: "space-between",
        }}
      >
        <Button onClick={handleClose} color="error" variant="outlined" disabled={loading} size="small">
          Cancel
        </Button>
        <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading} size="small">
          {loading ? <CircularProgress size={20} color="inherit" /> : "Save"}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default CreateLabPopup
