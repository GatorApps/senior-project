import { useEffect, useState } from "react"
import { axiosPrivate } from "../../apis/backend"
import HelmetComponent from "../../components/HelmetComponent/HelmetComponent"
import Header from "../../components/Header/Header"
import Footer from "../../components/Footer/Footer"
import SkeletonGroup from "../../components/SkeletonGroup/SkeletonGroup"
import ApplicationViewer from "../../components/ApplicationViewer/ApplicationViewer"
import {
  Box,
  Button,
  Container,
  Paper,
  Typography,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Alert,
  Snackbar,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  Divider,
  IconButton,
  Link,
} from "@mui/material"
import { Close as CloseIcon, OpenInNew as OpenInNewIcon } from "@mui/icons-material"
import { useNavigate } from "react-router-dom"

const ApplicationManagement = () => {
  // State management
  const [applications, setApplications] = useState({
    activeApplications: [],
    movingApplications: [],
    archivedApplications: [],
  })
  const [positions, setPositions] = useState([])
  const [loading, setLoading] = useState(true)
  const [positionsLoading, setPositionsLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedTab, setSelectedTab] = useState(0)
  const [selectedPosition, setSelectedPosition] = useState("")
  const [selectedApplication, setSelectedApplication] = useState(null)
  const [applicationStatus, setApplicationStatus] = useState("submitted")
  const [statusUpdating, setStatusUpdating] = useState(false)
  const [applicantInfo, setApplicantInfo] = useState({
    firstName: "",
    lastName: "",
    email: "",
  })
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "info",
  })

  const navigate = useNavigate()

  // Helper function to show snackbar notifications
  const showSnackbar = (message, severity = "info") => {
    setSnackbar({
      open: true,
      message,
      severity,
    })
  }

  // Close snackbar
  const handleCloseSnackbar = () => {
    setSnackbar((prev) => ({ ...prev, open: false }))
  }

  // Update application status from the popup
  const updateApplicationStatusFromPopup = async (newStatus) => {
    if (!selectedApplication) return

    setStatusUpdating(true)
    try {
      await axiosPrivate.put(`/application/applicationStatus`, null, {
        params: {
          labId: selectedApplication.labId,
          applicationId: selectedApplication.applicationId,
          status: newStatus,
        },
      })

      setApplicationStatus(newStatus)
      showSnackbar(`Application status updated to ${newStatus === "submitted" ? "active" : newStatus}`, "success")

      // Also update the application in the list
      fetchApplications()
    } catch (error) {
      console.error("Error updating status:", error)
      const errorMessage = error.response?.data?.message || "Error updating application status"
      setError(errorMessage)
      showSnackbar(errorMessage, "error")
    } finally {
      setStatusUpdating(false)
    }
  }

  // Update application status from the list
  const updateApplicationStatus = async (app, newStatus) => {
    try {
      setLoading(true)
      await axiosPrivate.put(`/application/applicationStatus`, null, {
        params: {
          labId: app.labId,
          applicationId: app.applicationId,
          status: newStatus,
        },
      })

      showSnackbar(`Application status updated to ${newStatus}`, "success")
      fetchApplications()
    } catch (error) {
      const errorMessage = error.response?.data?.message || "Error updating application status"
      setError(errorMessage)
      showSnackbar(errorMessage, "error")
      setLoading(false)
    }
  }

  // Fetch applications for selected position
  const fetchApplications = async () => {
    if (!selectedPosition) {
      setApplications({ activeApplications: [], movingApplications: [], archivedApplications: [] })
      return
    }

    setLoading(true)
    try {
      const response = await axiosPrivate.get(`/application/applicationManagement?positionId=${selectedPosition}`)

      if (response.data && response.data.errCode === "0" && response.data.payload.applications) {
        setApplications(response.data.payload.applications)
        setError(null)
      } else {
        setApplications({ activeApplications: [], movingApplications: [], archivedApplications: [] })
        setError("No applications found for this position")
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message || "Error fetching applications"
      setError(errorMessage)
      showSnackbar(errorMessage, "error")
      setApplications({ activeApplications: [], movingApplications: [], archivedApplications: [] })
    } finally {
      setLoading(false)
    }
  }

  // Fetch positions on component mount
  useEffect(() => {
    const fetchPositions = async () => {
      setPositionsLoading(true)
      try {
        const response = await axiosPrivate.get("/posting/postingsList")

        if (response.data && response.data.errCode === "0" && response.data.payload.positions) {
          const positionsList = response.data.payload.positions
          setPositions(positionsList)

          // Set the first position as selected if available
          if (positionsList.length > 0) {
            setSelectedPosition(positionsList[0].positionId)
          } else {
            setError("No positions available")
            setSelectedPosition("")
          }
        } else {
          setError("No positions found")
          setPositions([])
          setSelectedPosition("")
        }
      } catch (error) {
        const errorMessage = error.response?.data?.message || "Error fetching positions"
        setError(errorMessage)
        showSnackbar(errorMessage, "error")
        setPositions([])
        setSelectedPosition("")
      } finally {
        setPositionsLoading(false)
      }
    }

    fetchPositions()
  }, [])

  // Fetch applications when a position is selected
  useEffect(() => {
    if (selectedPosition) {
      fetchApplications()
    }
  }, [selectedPosition])

  // Handle viewing an application
  const handleViewApplication = (application) => {
    setSelectedApplication(application)
    setApplicationStatus(application.status || "submitted")

    // Set applicant info
    setApplicantInfo({
      firstName: application.firstName || "",
      lastName: application.lastName || "",
      email: application.email || "",
    })
  }

  // Handle closing the application viewer
  const handleCloseViewer = () => {
    setSelectedApplication(null)
  }

  // Handle tab change
  const handleTabChange = (index) => {
    setSelectedTab(index)
  }

  // Get current applications based on selected tab
  const getCurrentApplications = () => {
    switch (selectedTab) {
      case 0:
        return applications.activeApplications || []
      case 1:
        return applications.movingApplications || []
      case 2:
        return applications.archivedApplications || []
      default:
        return []
    }
  }

  // Format date for display
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleString()
    } catch (error) {
      return "Invalid date"
    }
  }

  // Common empty state container style
  const emptyStateContainerStyle = {
    textAlign: "center",
    py: 6,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "200px",
  }

  // Navigate to create position page
  const handleCreatePosition = () => {
    navigate("/posting/create")
  }

  return (
    <HelmetComponent title={"Application Management"}>
      <div className="ApplicationManagement">
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              {/* Exactly match the GenericPage structure for the title area */}
              <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">Application Management</Typography>
                </Box>
                <Box
                  className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_right"
                  sx={{ "flex-grow": "1" }}
                >
                  <Box className="GenericPage__container_title_flexBox_right">
                    {/* Adjusted position, font size, and width */}
                    <FormControl
                      disabled={positionsLoading}
                      sx={{
                        backgroundColor: "white",
                        // Moved dropdown downward
                        position: "relative",
                        top: "0px", // Adjusted from -5px to 0px
                        width: 320, // Increased fixed width from 250px to 320px
                        "& .MuiOutlinedInput-notchedOutline": {
                          borderRadius: "4px",
                        },
                      }}
                    >
                      <InputLabel
                        id="position-select-label"
                        sx={{
                          fontSize: "1rem",
                          lineHeight: "1.5rem",
                          fontWeight: 500,
                          // Adjusted label position for new font size
                          transform: "translate(14px, 13px) scale(1)",
                          "&.MuiInputLabel-shrink": {
                            transform: "translate(14px, -9px) scale(0.75)",
                          },
                        }}
                      >
                        Position
                      </InputLabel>
                      <Select
                        labelId="position-select-label"
                        id="position-select"
                        value={selectedPosition}
                        onChange={(e) => setSelectedPosition(e.target.value)}
                        sx={{
                          width: "100%", // Use full width of parent
                          height: "46px", // Maintained height
                          fontSize: "1rem", // Increased font size from 0.875rem
                          fontWeight: 500, // Added slightly bolder font
                          "& .MuiSelect-select": {
                            // Adjusted padding for new font size
                            paddingTop: "10px",
                            paddingBottom: "10px",
                            // Ensure text truncation
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                            whiteSpace: "nowrap",
                          },
                        }}
                        label="Position"
                        MenuProps={{
                          PaperProps: {
                            style: {
                              maxHeight: 300,
                              width: 320, // Match the width of the dropdown
                            },
                          },
                        }}
                      >
                        {positionsLoading ? (
                          <MenuItem disabled>Loading positions...</MenuItem>
                        ) : positions.length === 0 ? (
                          <MenuItem disabled>No positions available</MenuItem>
                        ) : (
                          positions.map((pos) => (
                            <MenuItem
                              key={pos.positionId}
                              value={pos.positionId}
                              sx={{
                                fontSize: "1rem", // Increased font size
                                fontWeight: 500, // Added slightly bolder font
                                // Ensure text truncation in dropdown items
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                                whiteSpace: "nowrap",
                                maxWidth: "100%",
                              }}
                              title={pos.name} // Show full name on hover
                            >
                              {pos.name}
                            </MenuItem>
                          ))
                        )}
                      </Select>
                    </FormControl>
                  </Box>
                </Box>
              </Box>
            </Container>

            <Container maxWidth="lg">
              {/* Error Display */}
              {error && !loading && (
                <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
                  {error}
                </Alert>
              )}

              {/* Application List - Using the same Paper class as GenericPage */}
              <Paper className="GenericPage__container_paper" variant="outlined" sx={{ padding: "24px" }}>
                {positions.length === 0 && !positionsLoading ? (
                  // No positions available state
                  <Box sx={emptyStateContainerStyle}>
                    <Typography variant="h6" color="textSecondary" sx={{ mb: 2 }}>
                      You have not posted any positions yet
                    </Typography>
                    <Typography variant="h7" color="textSecondary">
                      Start by{" "}
                      <Link
                        href="/posting/create"
                        onClick={(e) => {
                          e.preventDefault()
                          handleCreatePosition()
                        }}
                        sx={{
                          color: "primary.main",
                          textDecoration: "none",
                          "&:hover": {
                            textDecoration: "underline",
                          },
                          display: "inline-flex",
                          alignItems: "center",
                        }}
                      >
                        posting a position
                        <OpenInNewIcon sx={{ ml: 0.5, fontSize: "1rem" }} />
                      </Link>
                    </Typography>
                  </Box>
                ) : (
                  <>
                    {/* Updated Tabs to match the image - made smaller */}
                    <Box
                      sx={{
                        width: "100%",
                        display: "flex",
                        justifyContent: "center",
                        mb: 4,
                      }}
                    >
                      <Box
                        sx={{
                          display: "flex",
                          width: "fit-content",
                          maxWidth: "100%",
                          border: "1px solid #1e4b94",
                          borderRadius: "4px",
                          overflow: "hidden",
                        }}
                      >
                        {["Active", "Moving Forward", "Archived"].map((label, index) => (
                          <Box
                            key={index}
                            onClick={() => handleTabChange(index)}
                            sx={{
                              padding: "6px 16px", // Reduced padding for smaller height
                              minWidth: "100px", // Reduced minimum width
                              textAlign: "center",
                              cursor: "pointer",
                              backgroundColor: selectedTab === index ? "#1e4b94" : "transparent",
                              color: selectedTab === index ? "white" : "#1e4b94",
                              fontWeight: 500,
                              fontSize: "0.875rem", // Reduced font size
                              borderRight: index < 2 ? "1px solid #1e4b94" : "none",
                              borderColor: "rgba(40, 87, 151, 0.5)",
                              transition: "background-color 0.3s, color 0.3s",
                              "&:hover": {
                                backgroundColor: selectedTab === index ? "#1e4b94" : "#f0f4fa",
                              },
                            }}
                          >
                            {label}
                          </Box>
                        ))}
                      </Box>
                    </Box>

                    {/* Loading State */}
                    {loading ? (
                      <SkeletonGroup boxPadding={"0"} />
                    ) : (
                      <>
                        {/* Empty State */}
                        {getCurrentApplications().length === 0 ? (
                          <Box sx={emptyStateContainerStyle}>
                            <Typography variant="h6" color="textSecondary" sx={{ mb: 2 }}>
                              No applications available in this category
                            </Typography>
                          </Box>
                        ) : (
                          /* Application List */
                          getCurrentApplications().map((app) => (
                            <Box
                              key={app.applicationId || `${app.opid}-${app.email}`}
                              sx={{
                                mb: 2,
                                p: 2,
                                borderRadius: 1,
                                border: "1px solid #eee",
                                "&:hover": {
                                  backgroundColor: "#f9f9f9",
                                },
                              }}
                            >
                              <Box
                                sx={{
                                  display: "flex",
                                  flexDirection: { xs: "column", md: "row" }, // Stack on mobile and tablet, row on desktop
                                  justifyContent: "space-between",
                                  width: "100%",
                                  alignItems: { xs: "flex-start", md: "flex-start" },
                                  gap: { xs: 2, md: 1 },
                                }}
                              >
                                <Box
                                  sx={{
                                    flexGrow: 1,
                                    minWidth: 0,
                                    mr: { xs: 0, md: 2 }, // No margin on mobile/tablet, margin on desktop
                                    width: "100%", // Full width on all screens
                                  }}
                                >
                                  <Typography
                                    variant="h6"
                                    sx={{
                                      overflow: "hidden",
                                      textOverflow: "ellipsis",
                                      whiteSpace: "nowrap",
                                      maxWidth: "100%",
                                    }}
                                    title={app.firstName ? `${app.firstName} ${app.lastName}` : "Unnamed Applicant"} // Shows full text on hover
                                  >
                                    {app.firstName ? `${app.firstName} ${app.lastName}` : "Unnamed Applicant"}
                                  </Typography>
                                  <Typography
                                    variant="body2"
                                    sx={{
                                      overflow: "hidden",
                                      textOverflow: "ellipsis",
                                      whiteSpace: "nowrap",
                                      maxWidth: "100%",
                                    }}
                                    title={app.email || "No email provided"} // Shows full text on hover
                                  >
                                    {app.email || "No email provided"}
                                  </Typography>
                                  <Typography variant="body2" color="textSecondary">
                                    Submitted: {formatDate(app.submissionTimeStamp)}
                                  </Typography>
                                </Box>

                                <Box
                                  sx={{
                                    display: "flex",
                                    gap: 2,
                                    alignItems: "center",
                                    flexShrink: 0,
                                    width: "auto",
                                    justifyContent: "flex-start",
                                  }}
                                >
                                  <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={() => handleViewApplication(app)}
                                  >
                                    View
                                  </Button>

                                  {/* Fixed width for status dropdown */}
                                  <FormControl sx={{ width: 170 }}>
                                    <Select
                                      value={app.status || "submitted"}
                                      onChange={(e) => updateApplicationStatus(app, e.target.value)}
                                      size="small"
                                      sx={{
                                        width: "100%",
                                        "& .MuiSelect-select": {
                                          overflow: "hidden",
                                          textOverflow: "ellipsis",
                                          whiteSpace: "nowrap",
                                        },
                                      }}
                                      MenuProps={{
                                        PaperProps: {
                                          style: {
                                            width: 170,
                                          },
                                        },
                                      }}
                                    >
                                      <MenuItem value="submitted">Active</MenuItem>
                                      <MenuItem value="moving forward">Moving Forward</MenuItem>
                                      <MenuItem value="archived">Archived</MenuItem>
                                    </Select>
                                  </FormControl>
                                </Box>
                              </Box>
                            </Box>
                          ))
                        )}
                      </>
                    )}
                  </>
                )}
              </Paper>
            </Container>

            {/* Application Viewer Modal */}
            <Dialog
              open={Boolean(selectedApplication)}
              onClose={handleCloseViewer}
              maxWidth="md"
              fullWidth
              PaperProps={{
                sx: {
                  height: "80vh",
                  display: "flex",
                  flexDirection: "column",
                },
              }}
            >
              <DialogTitle
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  pb: 2,
                  pr: 1,
                }}
              >
                <Box sx={{ flexGrow: 1, minWidth: 0, mr: 2 }}>
                  <Typography
                    variant="subtitle1"
                    sx={{
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                      maxWidth: "100%",
                    }}
                    title={
                      applicantInfo.firstName
                        ? `${applicantInfo.firstName} ${applicantInfo.lastName}`
                        : "Applicant Details"
                    }
                  >
                    {applicantInfo.firstName
                      ? `${applicantInfo.firstName} ${applicantInfo.lastName}`
                      : "Applicant Details"}
                  </Typography>
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                      maxWidth: "100%",
                    }}
                    title={applicantInfo.email || "No email provided"}
                  >
                    {applicantInfo.email || "No email provided"}
                  </Typography>
                </Box>

                <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                  <FormControl sx={{ width: 180 }}>
                    <Select
                      value={applicationStatus}
                      onChange={(e) => updateApplicationStatusFromPopup(e.target.value)}
                      size="small"
                      disabled={statusUpdating}
                      sx={{
                        height: 36,
                        "& .MuiSelect-select": {
                          display: "flex",
                          alignItems: "center",
                          paddingRight: statusUpdating ? "32px" : "32px", // Keep consistent padding
                        },
                      }}
                      MenuProps={{
                        PaperProps: {
                          style: { width: 180 },
                        },
                      }}
                      // Render value with loading indicator inside
                      renderValue={(selected) => (
                        <Box sx={{ display: "flex", alignItems: "center", position: "relative" }}>
                          {selected === "submitted"
                            ? "Active"
                            : selected === "moving forward"
                              ? "Moving Forward"
                              : "Archived"}
                          {statusUpdating && (
                            <CircularProgress
                              size={16}
                              sx={{
                                position: "absolute",
                                right: -20,
                                color: "primary.main",
                              }}
                            />
                          )}
                        </Box>
                      )}
                    >
                      <MenuItem value="submitted">Active</MenuItem>
                      <MenuItem value="moving forward">Moving Forward</MenuItem>
                      <MenuItem value="archived">Archived</MenuItem>
                    </Select>
                  </FormControl>

                  <IconButton
                    onClick={handleCloseViewer}
                    size="small"
                    aria-label="close"
                    sx={{
                      ml: 1,
                      "&:hover": {
                        backgroundColor: "rgba(0, 0, 0, 0.04)",
                      },
                    }}
                  >
                    <CloseIcon />
                  </IconButton>
                </Box>
              </DialogTitle>

              <Divider />

              <DialogContent
                sx={{
                  flex: 1,
                  p: 0,
                  overflow: "hidden",
                }}
              >
                {/* Render the ApplicationViewer component */}
                {selectedApplication && (
                  <ApplicationViewer application={selectedApplication} applicantInfo={applicantInfo} />
                )}
              </DialogContent>
            </Dialog>

            {/* Snackbar for notifications */}
            <Snackbar
              open={snackbar.open}
              autoHideDuration={6000}
              onClose={handleCloseSnackbar}
              anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
            >
              <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: "100%" }}>
                {snackbar.message}
              </Alert>
            </Snackbar>
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent>
  )
}

export default ApplicationManagement
