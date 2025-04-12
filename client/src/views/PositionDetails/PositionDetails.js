import { useState, useEffect } from "react"
import { useSelector } from "react-redux"
import { useLocation, useNavigate } from "react-router-dom"
import { axiosPrivate } from "../../apis/backend"
import HelmetComponent from "../../components/HelmetComponent/HelmetComponent"
import Header from "../../components/Header/Header"
import Footer from "../../components/Footer/Footer"
import SkeletonGroup from "../../components/SkeletonGroup/SkeletonGroup"
import Box from "@mui/material/Box"
import Button from "@mui/material/Button"
import Container from "@mui/material/Container"
import Paper from "@mui/material/Paper"
import Typography from "@mui/material/Typography"
import Link from "@mui/material/Link"
import Alert from "@mui/material/Alert"
import Divider from "@mui/material/Divider"
import ApplicationPopup from "../../components/ApplicationPopup/ApplicationPopup"
import OpenInNewIcon from "@mui/icons-material/OpenInNew"

const PostingDetails = () => {
  const userInfo = useSelector((state) => state.auth.userInfo)
  const navigate = useNavigate()

  // Extract postingId and apply from URL
  const location = useLocation()
  const searchParams = new URLSearchParams(location.search)
  const postingId = searchParams.get("postingId")
  const shouldApply = searchParams.get("apply") === "1"

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [postingDetails, setPostingDetails] = useState(null)
  const [openApplyModal, setOpenApplyModal] = useState(false)
  const [applicationQuestions, setApplicationQuestions] = useState([])
  const [alreadyApplied, setAlreadyApplied] = useState(false)

  // Format date with error handling
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleDateString()
    } catch (error) {
      return "N/A"
    }
  }

  const checkAlreadyApplied = async () => {
    if (!postingId) return

    try {
      const response = await axiosPrivate.get(`/application/alreadyApplied?positionId=${postingId}`)
      if (response.data.errCode === "0") {
        setAlreadyApplied(response.data.payload.alreadyApplied)
      }
    } catch (error) {
      console.error("Error checking application status:", error)
      // Don't set error state here to avoid blocking the main content
    }
  }

  // Fetch posting details
  useEffect(() => {
    if (!postingId) {
      setError("No position ID provided")
      setLoading(false)
      return
    }

    const fetchPostingData = async () => {
      try {
        const detailsResponse = await axiosPrivate.get(`/posting?positionId=${postingId}`)

        if (detailsResponse.data && detailsResponse.data.payload) {
          setPostingDetails(detailsResponse.data.payload.position || {})
        } else {
          throw new Error("Invalid response format")
        }

        // Fetch application questions
        const questionsResponse = await axiosPrivate.get(`posting/supplementalQuestions?positionId=${postingId}`)
        if (questionsResponse.data && questionsResponse.data.payload) {
          setApplicationQuestions(questionsResponse.data.payload.position.applicationQuestions)
        }

        // Check if the user has already applied
        await checkAlreadyApplied()
      } catch (error) {
        console.error("Error fetching position details:", error)
        setError(error.response?.data?.message || error.message || "Failed to fetch position details")
      } finally {
        setLoading(false)
      }
    }

    fetchPostingData()
  }, [postingId])

  // Open apply modal if URL has apply=1
  useEffect(() => {
    if (shouldApply && !loading && !error && postingDetails && !alreadyApplied) {
      setOpenApplyModal(true)

      // Remove the apply=1 parameter from the URL while preserving other parameters
      const updatedSearchParams = new URLSearchParams(location.search)
      updatedSearchParams.delete("apply")

      // Create the new URL with the updated search params
      const newUrl = location.pathname + (updatedSearchParams.toString() ? `?${updatedSearchParams.toString()}` : "")

      // Update the URL without reloading the page
      navigate(newUrl, { replace: true })
    }
  }, [shouldApply, loading, error, postingDetails, alreadyApplied, navigate, location])

  // Determine button text and action based on position status and application status
  const getButtonProps = () => {
    if (alreadyApplied) {
      return {
        text: "Track My Applications",
        onClick: () => navigate("/myapplications"),
        variant: "outlined",
        disabled: false,
      }
    } else if (postingDetails?.status === "open") {
      return {
        text: "Apply",
        onClick: () => setOpenApplyModal(true),
        variant: "contained",
        disabled: false,
      }
    } else {
      return {
        text: "Browse Open Opportunities",
        onClick: () => navigate("/search"),
        variant: "outlined",
        disabled: false,
      }
    }
  }

  return (
    <HelmetComponent title={postingDetails?.positionName || "Position Details"}>
      <div className="PostingDetailsPage">
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box
                className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left"
                sx={{ maxWidth: 1000, mx: "auto" }}
              >
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">{postingDetails?.positionName || "Position Details"}</Typography>
                </Box>
              </Box>
            </Container>
            <Container maxWidth="lg">
              <Paper
                className="PostingDetailsPage__container"
                variant="outlined"
                sx={{
                  padding: "24px",
                  marginBottom: 4,
                  maxWidth: 1000,
                  mx: "auto",
                }}
              >
                {loading && (
                  <>
                    <SkeletonGroup />
                    <SkeletonGroup />
                  </>
                )}

                {!loading && error && (
                  <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                  </Alert>
                )}

                {!loading && !error && !postingDetails && (
                  <Alert severity="warning" sx={{ mb: 3 }}>
                    No position information found
                  </Alert>
                )}

                {!loading && !error && postingDetails && (
                  <>
                    {/* Posted Date Information */}
                    <Typography variant="body2" color="textSecondary" sx={{ marginBottom: 3 }}>
                      Posted: {formatDate(postingDetails.postedTimeStamp)}
                      {postingDetails.lastUpdatedTimeStamp &&
                        postingDetails.lastUpdatedTimeStamp !== postingDetails.postedTimeStamp && (
                          <> &nbsp;| &nbsp;Last Updated: {formatDate(postingDetails.lastUpdatedTimeStamp)}</>
                        )}
                    </Typography>

                    {/* Lab Name */}
                    {postingDetails.labName && (
                      <Box sx={{ marginBottom: 3 }}>
                        <Typography
                          variant="h6"
                          color="rgb(191, 68, 24)"
                          sx={{ fontSize: "18px", fontWeight: "bold", mb: 1.5 }}
                        >
                          Lab
                        </Typography>
                        <Link
                          href={`/lab?labId=${postingDetails.labId}`}
                          target="_blank"
                          underline="hover"
                          sx={{ display: "flex", alignItems: "center" }}
                        >
                          <Typography variant="body1" color="primary">
                            {postingDetails.labName}
                          </Typography>
                          <OpenInNewIcon sx={{ ml: 0.5, fontSize: "0.9rem" }} />
                        </Link>
                        <Divider sx={{ mt: 3, mb: 3 }} />
                      </Box>
                    )}

                    {/* Job Description */}
                    <Box sx={{ marginBottom: 4 }}>
                      <Typography
                        variant="h6"
                        color="rgb(191, 68, 24)"
                        sx={{ fontSize: "18px", fontWeight: "bold", mb: 2 }}
                      >
                        About the Position
                      </Typography>
                      {postingDetails.positionDescription ? (
                        <Box
                          dangerouslySetInnerHTML={{ __html: postingDetails.positionDescription }}
                          sx={{ color: "text.secondary" }}
                        />
                      ) : (
                        <Typography variant="body1" color="text.secondary">
                          No description available
                        </Typography>
                      )}
                      <Divider sx={{ mt: 4, mb: 4 }} />
                    </Box>

                    {/* Actions - Apply */}
                    <Box sx={{ marginTop: "24px" }}>
                      {/* Show alerts first */}
                      {alreadyApplied && (
                        <Alert severity="success" sx={{ mb: 3, maxWidth: "fit-content" }}>
                          You have already applied for this position
                        </Alert>
                      )}

                      {!alreadyApplied && postingDetails.status !== "open" && (
                        <Alert severity="warning" sx={{ mb: 3, maxWidth: "fit-content" }}>
                          This position is no longer accepting applications
                        </Alert>
                      )}

                      {/* Then show the button with dynamic properties */}
                      <Button
                        variant={getButtonProps().variant}
                        color="primary"
                        onClick={getButtonProps().onClick}
                        disabled={getButtonProps().disabled}
                      >
                        {getButtonProps().text}
                      </Button>
                    </Box>
                  </>
                )}
              </Paper>
            </Container>
          </Box>
        </main>

        <Footer />

        {/* Apply Modal */}
        {postingDetails && (
          <ApplicationPopup
            open={openApplyModal}
            questions={applicationQuestions}
            onClose={() => {
              setOpenApplyModal(false)
              checkAlreadyApplied()
            }}
            postingId={postingId}
            labId={postingDetails.labId}
            positionName={postingDetails.positionName}
            labName={postingDetails.labName}
            userInfo={userInfo}
          />
        )}
      </div>
    </HelmetComponent>
  )
}

export default PostingDetails
