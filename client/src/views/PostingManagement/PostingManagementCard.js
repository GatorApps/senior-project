import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardActions,
  CardContent,
  Button,
  Typography,
  Link,
  Alert,
  Skeleton,
  Divider
} from '@mui/material';
import { axiosPrivate } from '../../apis/backend';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';

const PostingManagementCard = () => {
  // State management
  const [postings, setPostings] = useState({ openPositions: [], closedPositions: [], archivedPositions: [] });
  const [loading, setLoading] = useState(true);
  const [tabIndex, setTabIndex] = useState(0);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPostings = async () => {
      try {
        setLoading(true);
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
        setPostings({ openPositions: [], closedPositions: [], archivedPositions: [] });
      } finally {
        setLoading(false);
      }
    };

    fetchPostings();
  }, []);

  const handleTabChange = (newIndex) => {
    setTabIndex(newIndex);
  };

  const getCurrentPostings = () => {
    switch (tabIndex) {
      case 0:
        return postings.openPositions || [];
      case 1:
        return postings.closedPositions || [];
      case 2:
        return postings.archivedPositions || [];
      default:
        return [];
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

  // Posting skeleton loading component
  const PostingSkeleton = ({ count = 3 }) => {
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
          {/* Lab name skeleton */}
          <Skeleton variant="text" width="40%" height={20} sx={{ mb: 0.5 }} />

          {/* Position name skeleton */}
          <Skeleton variant="text" width="60%" height={24} sx={{ mb: 0.5 }} />

          {/* Date skeleton */}
          <Skeleton variant="text" width="40%" height={20} />
        </Box>
      ));
  };

  const emptyStateContainerStyle = {
    textAlign: "center",
    py: 3,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "150px",
  };

  // Render empty state message based on current tab
  const renderEmptyState = () => {
    if (tabIndex === 0) {
      // Special message for Open tab
      return (
        <Box sx={emptyStateContainerStyle}>
          <Typography variant="body1" color="textSecondary" sx={{ mb: 1 }}>
            No positions available in this category
          </Typography>
          <Typography variant="body1" color="textSecondary">
            Start by{" "}
            <Link
              href="/postingeditor"
              onClick={(e) => {
                e.preventDefault();
                navigate("/postingeditor");
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
      );
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
            No positions available in this category
          </Typography>
        </Box>
      );
    }
  };

  return (
    <Box sx={{ width: '100%' }}>
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
            My Postings
          </Typography>

          {/* Error Display */}
          {error && !loading && (
            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
              {error}
            </Alert>
          )}

          {/* Custom Tabs - Styled to match MyApplicationsCard */}
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
              {["Open", "Closed", "Archived"].map((label, index) => (
                <Box
                  key={index}
                  onClick={() => handleTabChange(index)}
                  sx={{
                    padding: "4px 12px",
                    minWidth: "80px",
                    textAlign: "center",
                    cursor: "pointer",
                    backgroundColor: tabIndex === index ? "#1e4b94" : "transparent",
                    color: tabIndex === index ? "white" : "#1e4b94",
                    fontWeight: 500,
                    fontSize: "0.85rem",
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

          {/* Postings List */}
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
              <PostingSkeleton count={3} />
            ) : (
              <>
                {/* Empty State */}
                {getCurrentPostings().length === 0
                  ? renderEmptyState()
                  : /* Postings List - Limited to 3 items */
                  getCurrentPostings()
                    .slice(0, 3)
                    .map((posting, index, array) => (
                      <Box
                        key={posting.positionId || `posting-${index}`}
                        sx={{
                          p: 1.5,
                          borderBottom: index < array.length - 1 ? "1px solid #ddd" : "none",
                          '&:hover': {
                            backgroundColor: 'rgba(0, 0, 0, 0.04)',
                          },
                        }}
                      >
                        {/* Lab Name */}
                        <Typography
                          variant="body2"
                          color="textSecondary"
                          sx={{
                            fontSize: "0.75rem",
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                            whiteSpace: "nowrap",
                            maxWidth: "100%",
                          }}
                          title={posting.labName || "Unknown Lab"}
                        >
                          {posting.labName || "Unknown Lab"}
                        </Typography>

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
                          title={posting.name || "Unnamed Position"}
                        >
                          {posting.name || "Unnamed Position"}
                        </Typography>

                        {/* Last Updated */}
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
                          Last Updated: {formatDate(posting.postedTimeStamp)}
                        </Typography>
                      </Box>
                    ))}
              </>
            )}
          </Box>
        </CardContent>
        <CardActions>
          <Button size="medium" onClick={() => navigate('/postingeditor')}>Post New</Button>
          <Button size="medium" onClick={() => navigate('/postingmanagement')}>Manage All Postings</Button>
        </CardActions>
      </Card>
    </Box>
  );
};

export default PostingManagementCard
