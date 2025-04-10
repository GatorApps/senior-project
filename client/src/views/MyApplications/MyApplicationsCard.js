import { useEffect, useState } from "react"
import { axiosPrivate } from "../../apis/backend"
import { useSelector } from "react-redux"
import { useNavigate } from "react-router-dom"
import { Box, Card, CardActions, CardContent, Button, Typography, Alert, Skeleton, Link } from "@mui/material"
import OpenInNewIcon from "@mui/icons-material/OpenInNew"

const MyApplicationsCard = () => {
  // State management
  const userInfo = useSelector((state) => state.auth.userInfo)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [data, setData] = useState({
    activeApplications: [],
    movingApplications: [],
    archivedApplications: [],
  })
  const [tabValue, setTabValue] = useState(0)
  const navigate = useNavigate()

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const response = await axiosPrivate.get("/application/studentList")

        if (response.data && response.data.payload && response.data.payload.applications) {
          // Ensure we have all categories
          setData({
            activeApplications: response.data.payload.applications.activeApplications || [],
            movingApplications: response.data.payload.applications.movingApplications || [],
            archivedApplications: response.data.payload.applications.archivedApplications || [],
          })
          setError(null)
        } else {
          setData({
            activeApplications: [],
            movingApplications: [],
            archivedApplications: [],
          })
          setError("No applications found")
        }
      } catch (error) {
        console.error(error)
        const errorMessage = error.response?.data?.message || "Error fetching applications"
        setError(errorMessage)
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

  // Get current applications based on selected tab
  const getCurrentApplications = () => {
    switch (tabValue) {
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

  // Format date for display
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleString()
    } catch (error) {
      return "Invalid date"
    }
  }

  // Get status color based on status
  const getStatusColor = (status) => {
    switch (status) {
      case "submitted":
        return "#1e4b94"
      case "moving forward":
        return "#2e7d32"
      case "archived":
        return "#616161"
      default:
        return "#616161"
    }
  }

  // Get status label based on status
  const getStatusLabel = (status) => {
    switch (status) {
      case "submitted":
        return "Active"
      case "moving forward":
        return "Moving Forward"
      case "archived":
        return "Archived"
      default:
        return "Unknown"
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
            p: 1.5,
            borderBottom: index < array.length - 1 ? "1px solid #ddd" : "none",
          }}
        >
          {/* Position name skeleton */}
          <Skeleton variant="text" width="60%" height={24} sx={{ mb: 0.5 }} />

          {/* Date skeleton */}
          <Skeleton variant="text" width="40%" height={20} />
        </Box>
      ))
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

  // Render empty state message based on current tab
  const renderEmptyState = () => {
    if (tabValue === 0) {
      // Special message for Active tab
      return (
        <Box sx={emptyStateContainerStyle}>
          <Typography variant="body1" color="textSecondary" sx={{ mb: 1 }}>
            No applications available in this category
          </Typography>
          <Typography variant="body1" color="textSecondary">
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
              <OpenInNewIcon sx={{ ml: 0.5, fontSize: "0.875rem" }} />
            </Link>
          </Typography>
        </Box>
      )
    } else {
      // Default message for other tabs
      return (
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
      )
    }
  }

  return (
    <Box sx={{ width: "100%" }}>
      <Card variant="outlined">
        <CardContent
          sx={{
            minHeight: "250px",
            display: "flex",
            flexDirection: "column",
            padding: "16px",
          }}
        >
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
            My Applications
          </Typography>

          {/* Error Display */}
          {error && !loading && (
            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
              {error}
            </Alert>
          )}

          {/* Custom Tabs */}
          <Box
            sx={{
              width: "100%",
              display: "flex",
              justifyContent: "center",
              mt: 0.5,
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
                  onClick={() => setTabValue(index)}
                  sx={{
                    padding: "4px 12px",
                    minWidth: "80px",
                    textAlign: "center",
                    cursor: "pointer",
                    backgroundColor: tabValue === index ? "#1e4b94" : "transparent",
                    color: tabValue === index ? "white" : "#1e4b94",
                    fontWeight: 500,
                    fontSize: "0.85rem",
                    borderRight: index < 2 ? "1px solid #1e4b94" : "none",
                    borderColor: "rgba(40, 87, 151, 0.5)",
                    transition: "background-color 0.3s, color 0.3s",
                    "&:hover": {
                      backgroundColor: tabValue === index ? "#1e4b94" : "#f0f4fa",
                    },
                  }}
                >
                  {label}
                </Box>
              ))}
            </Box>
          </Box>

          {/* Application List */}
          <Box
            sx={{
              padding: "12px",
              flexGrow: 1,
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
                {getCurrentApplications().length === 0
                  ? renderEmptyState()
                  : /* Application List - Limited to 3 items */
                  getCurrentApplications()
                    .slice(0, 3)
                    .map((app, index, array) => (
                      <Box
                        key={app.applicationId || `${app.positionId}-${index}`}
                        sx={{
                          p: 1.5,
                          borderBottom: index < array.length - 1 ? "1px solid #ddd" : "none",
                        }}
                      >
                        {/* Position Name */}
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
                          title={app.positionName || "Unnamed Position"}
                        >
                          {app.positionName || "Unnamed Position"}
                        </Typography>

                        {/* Submission Date and Status */}
                        <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
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
                          <Typography
                            sx={{
                              fontSize: "0.75rem",
                              color: getStatusColor(app.status || "submitted"),
                              fontWeight: 500,
                              ml: 1,
                            }}
                          >
                            {getStatusLabel(app.status || "submitted")}
                          </Typography>
                        </Box>
                      </Box>
                    ))}
              </>
            )}
          </Box>
        </CardContent>

        <CardActions>
          <Button size="medium" onClick={() => navigate("/myapplications")}>
            Track my applications
          </Button>
        </CardActions>
      </Card>
    </Box>
  )
}

export default MyApplicationsCard
