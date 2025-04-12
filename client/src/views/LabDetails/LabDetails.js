import React, { useEffect, useState } from "react"
import { axiosPrivate } from "../../apis/backend"
import { useSelector } from "react-redux"
import { useLocation, Link as RouterLink } from "react-router-dom"
import HelmetComponent from "../../components/HelmetComponent/HelmetComponent"
import Header from "../../components/Header/Header"
import Footer from "../../components/Footer/Footer"
import SkeletonGroup from "../../components/SkeletonGroup/SkeletonGroup"
import Box from "@mui/material/Box"
import Container from "@mui/material/Container"
import Paper from "@mui/material/Paper"
import Typography from "@mui/material/Typography"
import Link from "@mui/material/Link"
import Divider from "@mui/material/Divider"
import Alert from "@mui/material/Alert"
import List from "@mui/material/List"
import ListItem from "@mui/material/ListItem"
import IconButton from "@mui/material/IconButton"
import Tooltip from "@mui/material/Tooltip"
import { styled } from "@mui/material/styles"
import OpenInNewIcon from "@mui/icons-material/OpenInNew"
import ApplyIcon from "@mui/icons-material/AssignmentTurnedIn"

// Styled components matching exactly those in OpportunitySearch.js
const ResultItem = styled(ListItem)(({ theme }) => ({
  marginBottom: theme.spacing(1),
  borderRadius: theme.spacing(1),
  padding: theme.spacing(2),
  transition: "all 0.2s ease",
  "&:hover": {
    backgroundColor: theme.palette.action.hover,
  },
}))

const StyledDivider = styled(Divider)(({ theme }) => ({
  marginTop: theme.spacing(1),
  marginBottom: theme.spacing(1),
}))

const TruncatedTitle = styled(Typography)(({ theme }) => ({
  overflow: "hidden",
  textOverflow: "ellipsis",
  whiteSpace: "nowrap",
  width: "100%", // Take full width to ensure truncation works
  display: "block", // Block to ensure width constraint works
  color: theme.palette.primary.main,
  fontWeight: 500,
}))

const TitleContainer = styled(Box)(({ theme }) => ({
  flexGrow: 1,
  flexShrink: 1,
  minWidth: 0, // This is crucial for text-overflow to work in a flex container
  overflow: "hidden", // Ensure the container doesn't expand with long content
  marginRight: theme.spacing(2), // Add space between title and buttons
  display: "flex",
  flexDirection: "column", // Stack title and timestamp vertically
  maxWidth: "calc(100% - 100px)", // Ensure there's space for buttons on desktop
  [theme.breakpoints.down("sm")]: {
    maxWidth: "100%", // On mobile, take full width since buttons are hidden
  },
}))

const ActionButtonsContainer = styled(Box)(({ theme }) => ({
  display: "flex",
  flexShrink: 0, // Prevent the buttons from shrinking
  marginLeft: "auto",
  alignSelf: "flex-start", // Align to the top of the container
  [theme.breakpoints.down("sm")]: {
    display: "none", // Hide on mobile
  },
}))

const TitleLinkWrapper = styled(Box)(({ theme }) => ({
  width: "100%", // Take full width of parent
  overflow: "hidden", // Ensure overflow is hidden
}))

const ActionButton = styled(IconButton)(({ theme }) => ({
  border: "1px solid rgba(25, 118, 210, 0.5)",
  margin: theme.spacing(0, 0.5), // Reduced margin to bring buttons closer
  padding: theme.spacing(1.2), // Increased padding to make buttons larger
  transition: "all 0.2s ease",
  "&:hover": {
    backgroundColor: "rgba(25, 118, 210, 0.04)",
    border: "1px solid #1976d2",
  },
}))

// Custom styled Link component to limit clickable area (from OpportunitySearch.js)
const StyledLink = styled(RouterLink)(({ theme }) => ({
  textDecoration: "none",
  display: "inline-block", // Use inline-block to limit clickable area
  maxWidth: "100%", // Ensure it doesn't exceed container width
  "&:hover": {
    textDecoration: "none",
  },
}))

const LabDetails = ({ title }) => {
  const userInfo = useSelector((state) => state.auth.userInfo)
  const location = useLocation()

  // Extract the labid parameter from the URL
  const searchParams = new URLSearchParams(location.search)
  const labId = searchParams.get("labId")

  // Set loading state
  const [loading, setLoading] = useState(true)
  const [labDetails, setLabDetails] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    // Reset states when labId changes
    setLoading(true)
    setError(null)

    if (!labId) {
      setError("No lab ID provided")
      setLoading(false)
      return
    }

    // Fetch lab details
    axiosPrivate
      .get(`/lab?labId=${labId}`)
      .then((response) => {
        if (response.data && response.data.payload) {
          setLabDetails(response.data.payload)
        } else {
          throw new Error("Invalid response format")
        }
      })
      .catch((error) => {
        console.error("Error fetching lab details:", error)
        setError(error.response?.data?.message || error.message || "Failed to fetch lab details")
      })
      .finally(() => {
        setLoading(false)
      })
  }, [labId])

  // Format date for display
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleDateString()
    } catch (error) {
      return "N/A"
    }
  }

  // Function to truncate HTML content for preview
  const truncateHtml = (html, maxLength = 200) => {
    if (!html) return "No description available."

    // Create a temporary div to parse HTML
    const tempDiv = document.createElement("div")
    tempDiv.innerHTML = html

    // Get text content
    let text = tempDiv.textContent || tempDiv.innerText || ""

    // Truncate text
    if (text.length > maxLength) {
      text = text.substring(0, maxLength) + "..."
    }

    return text
  }

  // Check if title is likely to be truncated
  const isTitleLikelyTruncated = (title) => {
    return title && title.length > 40
  }

  return (
    <HelmetComponent title={labDetails?.lab?.labName || "Lab Details"}>
      <div className="GenericPage">
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box
                className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left"
                sx={{ maxWidth: 1000, mx: "auto" }}
              >
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">{labDetails?.lab?.labName || "Lab Details"}</Typography>
                </Box>
              </Box>
            </Container>
            <Container maxWidth="lg">
              <Paper
                className="GenericPage__container_paper"
                variant="outlined"
                sx={{ maxWidth: 1000, mx: "auto", p: 4, bgcolor: "background.paper" }}
              >
                {loading &&
                  <React.Fragment>
                    <SkeletonGroup />
                    <SkeletonGroup />
                    <SkeletonGroup />
                  </React.Fragment>
                }

                {!loading && error && (
                  <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                  </Alert>
                )}

                {!loading && !error && !labDetails && (
                  <Alert severity="warning" sx={{ mb: 3 }}>
                    No lab information found
                  </Alert>
                )}

                {!loading && labDetails && (
                  <Box>
                    {/* Lab Description */}
                    {labDetails.lab.labDescription && (
                      <React.Fragment>
                        <Typography variant="h6" color="rgb(191, 68, 24)" sx={{ fontSize: "18px", fontWeight: "bold", mb: 2 }}>
                          Description
                        </Typography>

                        <Typography
                          variant="body1"
                          sx={{ color: "text.secondary", mb: 3 }}
                          dangerouslySetInnerHTML={{ __html: labDetails.lab.labDescription }}
                        />
                        <Divider sx={{ mt: 4.5, mb: 3.5 }} />
                      </React.Fragment>
                    )}

                    {/* Lab Website */}
                    {labDetails.lab.website && (
                      <React.Fragment>
                        <Typography variant="h6" color="rgb(191, 68, 24)" sx={{ fontSize: "18px", fontWeight: "bold", mb: 1.5 }}>
                          Website
                        </Typography>

                        <Typography
                          variant="body1"
                          sx={{ color: "primary.main", mb: 3, display: "flex", alignItems: "center" }}
                        >
                          <Link
                            href={labDetails.lab.website}
                            target="_blank"
                            rel="noopener noreferrer"
                            sx={{ display: "flex", alignItems: "center" }}
                          >
                            {labDetails.lab.website}
                            <OpenInNewIcon sx={{ ml: 0.5, fontSize: "0.9rem" }} />
                          </Link>
                        </Typography>
                        <Divider sx={{ mt: 4.5, mb: 3.5 }} />
                      </React.Fragment>
                    )}

                    {/* Open Positions Section */}
                    <Typography variant="h6" color="rgb(191, 68, 24)" sx={{ fontSize: "18px", fontWeight: "bold" }}>
                      Open Opportunities
                    </Typography>

                    {labDetails.lab.positions && labDetails.lab.positions.length > 0 ? (
                      <List sx={{ width: "100%" }}>
                        {labDetails.lab.positions.map((position, index) => (
                          <React.Fragment key={position.positionId || index}>
                            <ResultItem>
                              <Box sx={{ width: "100%" }}>
                                {/* Position title with action buttons */}
                                <Box sx={{ display: "flex", alignItems: "flex-start", width: "100%" }}>
                                  <TitleContainer>
                                    <Tooltip
                                      title={position.positionName || "Untitled Position"}
                                      arrow
                                      disableHoverListener={!isTitleLikelyTruncated(position.positionName)}
                                    >
                                      <TitleLinkWrapper>
                                        {/* Use StyledLink component from OpportunitySearch.js */}
                                        <StyledLink to={`/posting?postingId=${position.positionId}`} target="_blank">
                                          <TruncatedTitle variant="h6" sx={{ fontSize: "1.1rem" }}>
                                            {position.positionName || "Untitled Position"}
                                          </TruncatedTitle>
                                        </StyledLink>
                                      </TitleLinkWrapper>
                                    </Tooltip>

                                    {/* Posted timestamp - no margin bottom */}
                                    <Typography variant="body2" color="text.secondary">
                                      Posted: {formatDate(position.postedTimeStamp)}
                                    </Typography>
                                  </TitleContainer>

                                  {/* Action buttons */}
                                  <ActionButtonsContainer>
                                    <Tooltip title="Apply" arrow>
                                      <ActionButton
                                        component={RouterLink}
                                        to={`/posting?postingId=${position.positionId}&apply=1`}
                                        target="_blank"
                                        size="medium"
                                        color="primary"
                                        aria-label="Apply"
                                        sx={{ mr: 0.5, width: "40px", height: "40px" }}
                                      >
                                        <ApplyIcon />
                                      </ActionButton>
                                    </Tooltip>

                                    <Tooltip title="View details" arrow>
                                      <ActionButton
                                        component={RouterLink}
                                        to={`/posting?postingId=${position.positionId}`}
                                        target="_blank"
                                        size="medium"
                                        color="primary"
                                        aria-label="View details"
                                        sx={{ width: "40px", height: "40px" }}
                                      >
                                        <OpenInNewIcon />
                                      </ActionButton>
                                    </Tooltip>
                                  </ActionButtonsContainer>
                                </Box>

                                {/* Position description preview */}
                                <Typography
                                  variant="body2"
                                  color="text.secondary"
                                  sx={{
                                    display: "-webkit-box",
                                    WebkitLineClamp: 3,
                                    WebkitBoxOrient: "vertical",
                                    overflow: "hidden",
                                    textOverflow: "ellipsis",
                                    mt: 1,
                                  }}
                                >
                                  {/* {truncateHtml(position.positionDescription)} */}
                                  {position.positionRawDescription}
                                </Typography>
                              </Box>
                            </ResultItem>
                            <StyledDivider component="li" />
                          </React.Fragment>
                        ))}
                      </List>
                    ) : (
                      <Typography variant="body1" sx={{ color: "text.secondary", mt: 1.5 }}>
                        No open positions available at this time
                      </Typography>
                    )}
                  </Box>
                )}
              </Paper>
            </Container>
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent>
  )
}

export default LabDetails
