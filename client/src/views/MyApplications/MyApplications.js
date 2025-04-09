"use client"

import { useEffect, useState } from "react"
import { useSelector } from "react-redux"
import HelmetComponent from "../../components/HelmetComponent/HelmetComponent"
import Header from "../../components/Header/Header"
import Footer from "../../components/Footer/Footer"
import ApplicationViewer from "../../components/ApplicationViewer/ApplicationViewer"
import SkeletonGroup from "../../components/SkeletonGroup/SkeletonGroup"
import {
  Box,
  Button,
  Container,
  Paper,
  Typography,
  Alert,
  Snackbar,
  Dialog,
  DialogTitle,
  DialogContent,
  Divider,
  IconButton,
  FormControl,
  Select,
} from "@mui/material"
import { Close as CloseIcon } from "@mui/icons-material"
import { axiosPrivate } from "../../apis/backend"
import MenuItem from "@mui/material/MenuItem"

const MyApplications = () => {
  // State management
  const userInfo = useSelector((state) => state.auth.userInfo)
  const [loading, setLoading] = useState(true)
  const [data, setData] = useState({
    activeApplications: [],
    movingApplications: [],
    archivedApplications: [],
  })
  const [error, setError] = useState(null)
  const [selectedTab, setSelectedTab] = useState(0)
  const [selectedApplication, setSelectedApplication] = useState(null)
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "info",
  })

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

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const response = await axiosPrivate.get("/application/studentList")

        if (response.data && response.data.payload && response.data.payload.applications) {
          // Ensure we have all three categories
          const applications = {
            activeApplications: response.data.payload.applications.activeApplications || [],
            movingApplications: response.data.payload.applications.movingApplications || [],
            archivedApplications: response.data.payload.applications.archivedApplications || [],
          }
          setData(applications)
          setError(null)
        } else {
          setData({
            activeApplications: [],
            movingApplications: [],
            archivedApplications: [],
          })
          setError("No applications found")
          showSnackbar("No applications found", "info")
        }
      } catch (error) {
        console.error(error)
        const errorMessage = error.response?.data?.message || "Error fetching applications"
        setError(errorMessage)
        showSnackbar(errorMessage, "error")
        setData({
          activeApplications: [],
          movingApplications: [],
          archivedApplications: [],
        })
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  // Handle viewing an application
  const handleViewApplication = (application) => {
    setSelectedApplication(application)
  }

  // Handle closing the application viewer
  const handleCloseViewer = () => {
    setSelectedApplication(null)
  }

  // Handle tab change
  const handleTabChange = (index) => {
    setSelectedTab(index)
  }

  // Format date for display
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleString()
    } catch (error) {
      return "Invalid date"
    }
  }

  // Get current applications based on selected tab
  const getCurrentApplications = () => {
    switch (selectedTab) {
      case 0:
        return data.activeApplications || []
      case 1:
        return data.movingApplications || []
      case 2:
        return data.archivedApplications || []
      default:
        return []
    }
  }

  return (
    <HelmetComponent title="My Applications">
      <div className="GenericPage">
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">My Applications</Typography>
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
                {/* Updated Tabs to match the ApplicationManagement */}
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
                      <Box sx={{ textAlign: "center", py: 4 }}>
                        <Typography variant="body1" color="textSecondary">
                          No applications available in this category
                        </Typography>
                      </Box>
                    ) : (
                      /* Application List - Exactly matching ApplicationManagement styling */
                      getCurrentApplications().map((app) => (
                        <Box
                          key={app.applicationId || `${app.positionId}-${app.submissionTimeStamp}`}
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
                                title={app.positionName || "Unnamed Position"} // Shows full text on hover
                              >
                                {app.positionName || "Unnamed Position"}
                              </Typography>
                              <Typography
                                variant="body2"
                                sx={{
                                  overflow: "hidden",
                                  textOverflow: "ellipsis",
                                  whiteSpace: "nowrap",
                                  maxWidth: "100%",
                                }}
                                title={app.labName || "No lab name provided"} // Shows full text on hover
                              >
                                {app.labName || "No lab name provided"}
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
                                // Don't fill the entire width on mobile/tablet
                                width: "auto",
                                justifyContent: "flex-start",
                              }}
                            >
                              <Button variant="contained" color="primary" onClick={() => handleViewApplication(app)}>
                                View
                              </Button>

                              {/* Fixed width for status display */}
                              <FormControl sx={{ width: 170 }}>
                                <Select
                                  value={app.status || "submitted"}
                                  disabled={true}
                                  size="small"
                                  sx={{
                                    width: "100%",
                                    "& .MuiSelect-select": {
                                      overflow: "hidden",
                                      textOverflow: "ellipsis",
                                      whiteSpace: "nowrap",
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
                    title={selectedApplication?.positionName || "Position Details"}
                  >
                    {selectedApplication?.positionName || "Position Details"}
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
                    title={selectedApplication?.labName || "Lab Details"}
                  >
                    {selectedApplication?.labName || "Lab Details"}
                  </Typography>
                </Box>

                <Box sx={{ display: "flex", alignItems: "center" }}>
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
                {selectedApplication && <ApplicationViewer application={selectedApplication} />}
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

export default MyApplications