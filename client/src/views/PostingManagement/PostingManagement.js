import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { axiosPrivate } from '../../apis/backend';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import {
  Box,
  Button,
  Container,
  Paper,
  Typography,
  FormControl,
  Select,
  MenuItem,
  Alert,
  Snackbar,
  CircularProgress,
  Link,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  OpenInNew as OpenInNewIcon,
  Edit as EditIcon,
  Assessment as AssessmentIcon
} from '@mui/icons-material';

const PostingManagement = () => {
  // State management
  const [postings, setPostings] = useState({ openPositions: [], closedPositions: [], archivedPositions: [] });
  const [loading, setLoading] = useState(true);
  const [updatingPositions, setUpdatingPositions] = useState({}); // Track updating status per position
  const [error, setError] = useState(null);
  const [tabIndex, setTabIndex] = useState(0);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'info'
  });

  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Helper function to show snackbar notifications
  const showSnackbar = (message, severity = 'info') => {
    setSnackbar({
      open: true,
      message,
      severity
    });
  };

  // Close snackbar
  const handleCloseSnackbar = () => {
    setSnackbar((prev) => ({ ...prev, open: false }));
  };

  const fetchPostings = async () => {
    setLoading(true);
    try {
      const response = await axiosPrivate.get('/posting/postingManagement');

      if (response.data && response.data.errCode === "0" &&
        response.data.payload && response.data.payload.postingsList) {
        setPostings(response.data.payload.postingsList);
        setError(null);
      } else {
        setError("No postings found");
        setPostings({ openPositions: [], closedPositions: [], archivedPositions: [] });
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message || "Error fetching postings";
      setError(errorMessage);
      showSnackbar(errorMessage, "error");
      setPostings({ openPositions: [], closedPositions: [], archivedPositions: [] });
    } finally {
      setLoading(false);
      // Clear all updating states when fetch completes
      setUpdatingPositions({});
    }
  };

  useEffect(() => {
    fetchPostings();
  }, []);

  const handleTabChange = (newIndex) => {
    setTabIndex(newIndex);
  };

  const filteredPostings = tabIndex === 0
    ? postings.openPositions
    : tabIndex === 1
      ? postings.closedPositions
      : postings.archivedPositions;

  const updatePositionStatus = async (posting, newStatus) => {
    // Set loading state for this specific position
    setUpdatingPositions(prev => ({
      ...prev,
      [posting.positionId]: true
    }));

    try {
      await axiosPrivate.put(`/posting/postingStatus`, null, {
        params: {
          positionId: posting.positionId,
          status: newStatus
        }
      });
      showSnackbar(`Position status updated to ${newStatus}`, "success");

      // Fetch updated postings (this will also clear all updating states)
      fetchPostings();
    } catch (error) {
      const errorMessage = error.response?.data?.message || "Error updating position status";
      setError(errorMessage);
      showSnackbar(errorMessage, "error");

      // Clear loading state for this position on error
      setUpdatingPositions(prev => ({
        ...prev,
        [posting.positionId]: false
      }));
    }
  };

  // Format date for display
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleString();
    } catch (error) {
      return "N/A";
    }
  };

  // Common empty state container style
  const emptyStateContainerStyle = {
    textAlign: "center",
    py: 6,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "200px",
  };

  // Handle creating a new position
  const handleCreatePosition = () => {
    navigate('/postingeditor');
  };

  // Navigate to application management for a specific position
  const handleReviewApplications = (positionId) => {
    navigate(`/applicationmanagement?positionId=${positionId}`);
  };

  // Get appropriate empty state message based on current tab
  const getEmptyStateMessage = () => {
    if (tabIndex === 0) {
      return (
        <>
          <Typography variant="h6" color="textSecondary" sx={{ mb: 2 }}>
            No positions available in this category
          </Typography>
          <Typography variant="h7" color="textSecondary">
            Start by{" "}
            <Link
              href="/postingeditor"
              onClick={(e) => {
                e.preventDefault();
                handleCreatePosition();
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
        </>
      );
    } else {
      return (
        <Typography variant="h6" color="textSecondary">
          No positions available in this category
        </Typography>
      );
    }
  };

  return (
    <HelmetComponent title={"Posting Management"}>
      <div className='PostingManagement'>
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">Posting Management</Typography>
                </Box>
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_right" sx={{ 'flex-grow': '1' }}>
                  <Box className="GenericPage__container_title_flexBox_right">
                    <Button
                      variant="contained"
                      size="medium"
                      onClick={handleCreatePosition}
                    >
                      Post new position
                    </Button>
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

              <Paper className="GenericPage__container_paper" variant="outlined" sx={{ padding: '24px' }}>
                {/* Tabs styled to exactly match ApplicationManagement */}
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
                    {["Open", "Closed", "Archived"].map((label, index) => (
                      <Box
                        key={index}
                        onClick={() => handleTabChange(index)}
                        sx={{
                          padding: "6px 16px", // Reduced padding for smaller height
                          minWidth: "100px", // Reduced minimum width
                          textAlign: "center",
                          cursor: "pointer",
                          backgroundColor: tabIndex === index ? "#1e4b94" : "transparent",
                          color: tabIndex === index ? "white" : "#1e4b94",
                          fontWeight: 500,
                          fontSize: "0.875rem", // Reduced font size
                          borderRight: index < 2 ? "1px solid #1e4b94" : "none",
                          borderColor: "rgba(40, 87, 151, 0.5)",
                          transition: "background-color 0.3s, color 0.3s",
                          "&:hover": {
                            backgroundColor: tabIndex === index ? "#1e4b94" : "#f0f4fa",
                          },
                        }}
                      >
                        {label}
                      </Box>
                    ))}
                  </Box>
                </Box>

                {loading ? (
                  <SkeletonGroup boxPadding={'0'} />
                ) : filteredPostings.length === 0 ? (
                  <Box sx={emptyStateContainerStyle}>
                    {getEmptyStateMessage()}
                  </Box>
                ) : (
                  filteredPostings.map((posting) => (
                    <Box
                      key={posting.positionId}
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
                          flexDirection: { xs: "column", md: "row" },
                          justifyContent: "space-between",
                          width: "100%",
                          alignItems: { xs: "flex-start", md: "center" },
                          gap: { xs: 2, md: 1 },
                        }}
                      >
                        <Box
                          sx={{
                            flexGrow: 1,
                            minWidth: 0,
                            mr: { xs: 0, md: 2 },
                            width: "100%",
                          }}
                        >
                          {/* Changed order to match requirement: position name, lab name, last updated */}
                          <Typography
                            variant="h6"
                            sx={{
                              overflow: "hidden",
                              textOverflow: "ellipsis",
                              whiteSpace: "nowrap",
                              maxWidth: "100%",
                              fontWeight: 500,
                            }}
                            title={posting.name || "Unnamed Position"}
                          >
                            {posting.name || "Unnamed Position"}
                          </Typography>
                          <Typography
                            variant="body2"
                            color="textSecondary"
                            sx={{
                              overflow: "hidden",
                              textOverflow: "ellipsis",
                              whiteSpace: "nowrap",
                              maxWidth: "100%",
                            }}
                            title={posting.labName || "Unknown Lab"}
                          >
                            {posting.labName || "Unknown Lab"}
                          </Typography>
                          <Typography variant="body2" color="textSecondary">
                            Last Updated: {formatDate(posting.postedTimeStamp)}
                          </Typography>
                        </Box>

                        <Box
                          sx={{
                            display: "flex",
                            flexDirection: "row",
                            gap: 1,
                            alignItems: "center",
                            flexShrink: 0,
                            justifyContent: { xs: "flex-start", md: "flex-end" },
                          }}
                        >
                          {/* Icon buttons for Edit and Review Applications */}
                          <Tooltip title="Edit Position">
                            <IconButton
                              color="primary"
                              onClick={() => navigate(`/postingeditor?postingId=${posting.positionId}`)}
                              sx={{
                                border: '1px solid rgba(25, 118, 210, 0.5)',
                                '&:hover': {
                                  backgroundColor: 'rgba(25, 118, 210, 0.04)',
                                  border: '1px solid #1976d2'
                                }
                              }}
                            >
                              <EditIcon />
                            </IconButton>
                          </Tooltip>

                          <Tooltip title="Review Applications">
                            <IconButton
                              color="primary"
                              onClick={() => handleReviewApplications(posting.positionId)}
                              sx={{
                                border: '1px solid rgba(25, 118, 210, 0.5)',
                                '&:hover': {
                                  backgroundColor: 'rgba(25, 118, 210, 0.04)',
                                  border: '1px solid #1976d2'
                                }
                              }}
                            >
                              <AssessmentIcon />
                            </IconButton>
                          </Tooltip>

                          <FormControl sx={{ width: 170, ml: 1 }}>
                            <Select
                              value={posting.status || "open"}
                              onChange={(e) => updatePositionStatus(posting, e.target.value)}
                              size="small"
                              disabled={updatingPositions[posting.positionId]} // Use position-specific loading state
                              sx={{
                                width: "100%",
                                "& .MuiSelect-select": {
                                  display: "flex",
                                  alignItems: "center",
                                  overflow: "hidden",
                                  textOverflow: "ellipsis",
                                  whiteSpace: "nowrap",
                                  paddingRight: updatingPositions[posting.positionId] ? "32px" : "32px",
                                },
                              }}
                              MenuProps={{
                                PaperProps: {
                                  style: {
                                    width: 170,
                                  },
                                },
                              }}
                              renderValue={(selected) => (
                                <Box sx={{ display: "flex", alignItems: "center", position: "relative" }}>
                                  {selected.charAt(0).toUpperCase() + selected.slice(1)}
                                  {updatingPositions[posting.positionId] && ( // Use position-specific loading state
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
                              <MenuItem value="open">Open</MenuItem>
                              <MenuItem value="closed">Closed</MenuItem>
                              <MenuItem value="archived">Archived</MenuItem>
                            </Select>
                          </FormControl>
                        </Box>
                      </Box>
                    </Box>
                  ))
                )}
              </Paper>
            </Container>
          </Box>
        </main>
        <Footer />

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
      </div>
    </HelmetComponent>
  );
}

export default PostingManagement
