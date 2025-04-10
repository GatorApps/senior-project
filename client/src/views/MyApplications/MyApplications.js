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
  Link,
} from "@mui/material"
import { Close as CloseIcon, OpenInNew as OpenInNewIcon } from "@mui/icons-material"
import { axiosPrivate } from "../../apis/backend"
import { useNavigate } from "react-router-dom"

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

  // Get status label and color based on status
  const getStatusInfo = (status) => {
    switch (status) {
      case "submitted":
        return { label: "Active", color: "#1e4b94", bgColor: "#e6f0ff" }
      case "moving forward":
        return { label: "Moving Forward", color: "#2e7d32", bgColor: "#e8f5e9" }
      case "archived":
        return { label: "Archived", color: "#616161", bgColor: "#f5f5f5" }
      default:
        return { label: "Unknown", color: "#616161", bgColor: "#f5f5f5" }
    }
  }

  // Common status chip styles
  const getStatusChipStyles = (statusInfo, isInDialog = false) => ({
    backgroundColor: statusInfo.bgColor,
    color: statusInfo.color,
    fontWeight: 500,
    width: isInDialog ? "120px" : "140px", // Reduced width but still fits "Moving Forward"
    height: isInDialog ? "32px" : "36px", // Match height with View button
    fontSize: isInDialog ? "0.875rem" : "0.9375rem", // Increased font size to match View button
    border: `1px solid ${statusInfo.color}20`,
    borderRadius: "4px", // Match button border radius
    "& .MuiChip-label": {
      padding: "0 8px",
      display: "flex",
      justifyContent: "center",
      width: "100%",
      lineHeight: isInDialog ? "32px" : "36px", // Match height for vertical centering
    },
  })

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
                    {getCurrentApplications().length === 0 ? (
                      <Box sx={emptyStateContainerStyle}>
                        <Typography variant="h6" color="textSecondary" sx={{ mb: 2 }}>
                          No applications available in this category
                        </Typography>
                        {selectedTab === 0 && (
                          <Typography variant="h7" color="textSecondary">
                            Start by{" "}
                            <Link
                              href="/search"
                              onClick={(e) => {
                                e.preventDefault()
                                navigate("/search")
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
                              searching for opportunities
                              <OpenInNewIcon sx={{ ml: 0.5, fontSize: "1rem" }} />
                            </Link>
                          </Typography>
                        )}
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
                              {/* Clickable Position Name */}
                              <Typography
                                variant="h6"
                                sx={{
                                  overflow: "hidden",
                                  textOverflow: "ellipsis",
                                  whiteSpace: "nowrap",
                                  maxWidth: "100%",
                                  display: "block",
                                }}
                                title={app.positionName || "Unnamed Position"} // Shows full text on hover
                              >
                                <Box
                                  component="span"
                                  sx={{
                                    "&:hover": {
                                      "& a": {
                                        textDecoration: "underline",
                                        color: "primary.main",
                                      },
                                      "& svg": {
                                        visibility: "visible",
                                      },
                                    },
                                  }}
                                >
                                  <Link
                                    href={`/posting?postingId=${app.positionId}`}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    sx={{
                                      color: "inherit",
                                      textDecoration: "none",
                                    }}
                                  >
                                    {app.positionName || "Unnamed Position"}
                                  </Link>
                                  <OpenInNewIcon
                                    sx={{
                                      fontSize: "0.875rem",
                                      ml: 0.5,
                                      verticalAlign: "middle",
                                      visibility: "hidden",
                                    }}
                                  />
                                </Box>
                              </Typography>

                              {/* Clickable Lab Name */}
                              <Typography
                                variant="body2"
                                sx={{
                                  overflow: "hidden",
                                  textOverflow: "ellipsis",
                                  whiteSpace: "nowrap",
                                  maxWidth: "100%",
                                  display: "block",
                                }}
                                title={app.labName || "No lab name provided"} // Shows full text on hover
                              >
                                <Box
                                  component="span"
                                  sx={{
                                    "&:hover": {
                                      "& a": {
                                        textDecoration: "underline",
                                        color: "primary.main",
                                      },
                                      "& svg": {
                                        visibility: "visible",
                                      },
                                    },
                                  }}
                                >
                                  <Link
                                    href={`/lab?labId=${app.labId}`}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    sx={{
                                      color: "inherit",
                                      textDecoration: "none",
                                    }}
                                  >
                                    {app.labName || "No lab name provided"}
                                  </Link>
                                  <OpenInNewIcon
                                    sx={{
                                      fontSize: "0.75rem",
                                      ml: 0.5,
                                      verticalAlign: "middle",
                                      visibility: "hidden",
                                    }}
                                  />
                                </Box>
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

                              {/* Styled Status Label */}
                              {(() => {
                                const statusInfo = getStatusInfo(app.status || "submitted")
                                return (
                                  <Box
                                    sx={{
                                      ...getStatusChipStyles(statusInfo),
                                      display: "flex",
                                      alignItems: "center",
                                      justifyContent: "center",
                                      textAlign: "center",
                                      fontSize: "0.9375rem", // Match View button font size
                                    }}
                                  >
                                    {statusInfo.label}
                                  </Box>
                                )
                              })()}
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
                  {/* Clickable Position Name in Dialog */}
                  <Typography
                    variant="subtitle1"
                    sx={{
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                      maxWidth: "100%",
                      display: "block",
                    }}
                    title={selectedApplication?.positionName || "Position Details"}
                  >
                    <Box
                      component="span"
                      sx={{
                        "&:hover": {
                          "& a": {
                            textDecoration: "underline",
                            color: "primary.main",
                          },
                          "& svg": {
                            visibility: "visible",
                          },
                        },
                      }}
                    >
                      <Link
                        href={selectedApplication ? `/posting?postingId=${selectedApplication.positionId}` : "#"}
                        target="_blank"
                        rel="noopener noreferrer"
                        sx={{
                          color: "inherit",
                          textDecoration: "none",
                        }}
                      >
                        {selectedApplication?.positionName || "Position Details"}
                      </Link>
                      <OpenInNewIcon
                        sx={{
                          fontSize: "0.875rem",
                          ml: 0.5,
                          verticalAlign: "middle",
                          visibility: "hidden",
                        }}
                      />
                    </Box>
                  </Typography>

                  {/* Clickable Lab Name in Dialog */}
                  <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                      maxWidth: "100%",
                      display: "block",
                    }}
                    title={selectedApplication?.labName || "Lab Details"}
                  >
                    <Box
                      component="span"
                      sx={{
                        "&:hover": {
                          "& a": {
                            textDecoration: "underline",
                            color: "primary.main",
                          },
                          "& svg": {
                            visibility: "visible",
                          },
                        },
                      }}
                    >
                      <Link
                        href={selectedApplication ? `/lab?labId=${selectedApplication.labId}` : "#"}
                        target="_blank"
                        rel="noopener noreferrer"
                        sx={{
                          color: "inherit",
                          textDecoration: "none",
                        }}
                      >
                        {selectedApplication?.labName || "Lab Details"}
                      </Link>
                      <OpenInNewIcon
                        sx={{
                          fontSize: "0.75rem",
                          ml: 0.5,
                          verticalAlign: "middle",
                          visibility: "hidden",
                        }}
                      />
                    </Box>
                  </Typography>
                </Box>

                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  {/* Status chip in dialog header */}
                  {selectedApplication &&
                    (() => {
                      const statusInfo = getStatusInfo(selectedApplication.status || "submitted")
                      return (
                        <Box
                          sx={{
                            ...getStatusChipStyles(statusInfo, true),
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            textAlign: "center",
                            fontSize: "0.875rem", // Increased font size while keeping cell size
                          }}
                        >
                          {statusInfo.label}
                        </Box>
                      )
                    })()}

                  <IconButton
                    onClick={handleCloseViewer}
                    size="small"
                    aria-label="close"
                    sx={{
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
