import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { axiosPrivate } from "../../apis/backend"
import {
  Box,
  Card,
  CardActions,
  CardContent,
  Button,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  Skeleton,
  Link,
} from "@mui/material"
import { OpenInNew as OpenInNewIcon } from "@mui/icons-material"

const ApplicationManagementCard = () => {
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
  const navigate = useNavigate()

  // Update application status
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
      fetchApplications()
    } catch (error) {
      const errorMessage = error.response?.data?.message || "Error updating application status"
      setError(errorMessage)
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

  // Application skeleton loading component
  const ApplicationSkeleton = ({ count = 3 }) => {
    return Array(count)
      .fill(0)
      .map((_, index, array) => (
        <Box
          key={`skeleton-${index}`}
          sx={{
            p: 1,
            borderBottom: index < array.length - 1 ? "1px solid #ddd" : "none",
          }}
        >
          {/* Name skeleton */}
          <Skeleton variant="text" width="60%" height={24} sx={{ mb: 0.5 }} />

          {/* Date skeleton */}
          <Skeleton variant="text" width="40%" height={20} />
        </Box>
      ))
  }

  // Navigate to create position page
  const handleCreatePosition = () => {
    navigate("/postingeditor")
  }

  // Common empty state container style
  const emptyStateContainerStyle = {
    textAlign: "center",
    py: 3,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "150px",
  }

  return (
    <Box sx={{ width: "100%" }}>
      <Card variant="outlined">
        <CardContent>
          <Typography
            gutterBottom
            variant="h2"
            component="div"
            sx={{
              lineHeight: 1.2,
              fontSize: "1.3125rem",
              fontWeight: 500,
              mb: 2,
            }}
          >
            Application Management
          </Typography>

          {positions.length === 0 && !positionsLoading ? (
            // No positions available state
            <Box sx={emptyStateContainerStyle}>
              <Typography variant="subtitle1" color="textSecondary" sx={{ mb: 1 }}>
                You have not posted any positions yet
              </Typography>
              <Typography variant="body1" color="textSecondary">
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
                  <OpenInNewIcon sx={{ ml: 0.5, fontSize: "0.875rem" }} />
                </Link>
              </Typography>
            </Box>
          ) : (
            <Box sx={{ mb: 2 }}>
              {/* Position Dropdown */}
              <FormControl
                disabled={positionsLoading}
                sx={{
                  backgroundColor: "white",
                  width: 250,
                  my: 1,
                  "& .MuiOutlinedInput-notchedOutline": {
                    borderRadius: "4px",
                  },
                }}
              >
                <InputLabel
                  id="position-select-label"
                  sx={{
                    fontSize: "1rem",
                    fontWeight: 500,
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
                    height: "40px",
                    fontSize: "0.875rem",
                    fontWeight: 500,
                    "& .MuiSelect-select": {
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
                        width: 250,
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
                          fontSize: "0.875rem",
                          fontWeight: 500,
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                          whiteSpace: "nowrap",
                          maxWidth: "100%",
                        }}
                        title={pos.name}
                      >
                        {pos.name}
                      </MenuItem>
                    ))
                  )}
                </Select>
              </FormControl>

              {/* Custom Tabs */}
              <Box
                sx={{
                  width: "100%",
                  display: "flex",
                  justifyContent: "center",
                  mt: 1.5,
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
                        padding: "4px 12px",
                        minWidth: "80px",
                        textAlign: "center",
                        cursor: "pointer",
                        backgroundColor: selectedTab === index ? "#1e4b94" : "transparent",
                        color: selectedTab === index ? "white" : "#1e4b94",
                        fontWeight: 500,
                        fontSize: "0.85rem",
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

              {/* Error Display */}
              {error && !loading && (
                <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
                  {error}
                </Alert>
              )}

              {/* Application List */}
              <Box
                sx={{
                  padding: "12px",
                  minHeight: "200px",
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "flex-start",
                }}
              >
                {/* Loading State with Skeleton */}
                {loading ? (
                  <ApplicationSkeleton count={3} />
                ) : (
                  <>
                    {/* Empty State */}
                    {getCurrentApplications().length === 0 ? (
                      <Box sx={emptyStateContainerStyle}>
                        <Typography
                          variant="body1"
                          color="textSecondary"
                          sx={{
                            textAlign: "center",
                            py: 2,
                          }}
                        >
                          No applications available in this category
                        </Typography>
                      </Box>
                    ) : (
                      /* Application List - Limited to 3 items */
                      getCurrentApplications()
                        .slice(0, 3)
                        .map((app, index, array) => (
                          <Box
                            key={app.applicationId || `${app.opid}-${app.email}`}
                            sx={{
                              p: 1.5,
                              borderBottom: index < array.length - 1 ? "1px solid #ddd" : "none",
                              "&:hover": {
                                backgroundColor: "rgba(0, 0, 0, 0.04)",
                              },
                            }}
                          >
                            <Typography
                              variant="subtitle1"
                              sx={{
                                fontWeight: 500,
                                fontSize: "1rem",
                                mb: 0.5,
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                                whiteSpace: "nowrap",
                                maxWidth: "100%",
                              }}
                            >
                              {app.firstName ? `${app.firstName} ${app.lastName}` : "Unnamed Applicant"}
                            </Typography>
                            <Typography
                              variant="body2"
                              color="textSecondary"
                              sx={{
                                fontSize: "0.75rem",
                                overflow: "hidden",
                                textOverflow: "ellipsis",
                                whiteSpace: "nowrap",
                              }}
                            >
                              Submitted: {formatDate(app.submissionTimeStamp)}
                            </Typography>
                          </Box>
                        ))
                    )}
                  </>
                )}
              </Box>
            </Box>
          )}
        </CardContent>
        <CardActions>
          <Button size="medium" onClick={() => navigate("/applicationmanagement")}>
            Manage All Applications
          </Button>
        </CardActions>
      </Card>
    </Box>
  )
}

export default ApplicationManagementCard
