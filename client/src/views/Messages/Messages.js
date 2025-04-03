import React, { useEffect, useState, useRef } from 'react';
import { axiosPrivate } from '../../apis/backend';
import { useDispatch, useSelector } from 'react-redux';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import {
  Box,
  Typography,
  Paper,
  List,
  ListItem,
  ListItemText,
  Divider,
  CircularProgress,
  IconButton,
  Badge,
  Grid,
  Button,
  Pagination,
  Snackbar,
  Alert,
  Container,
  useMediaQuery,
  useTheme,
  Skeleton,
  Tooltip
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { formatDistanceToNow } from 'date-fns';
import MarkEmailReadIcon from '@mui/icons-material/MarkEmailRead';
import MarkEmailUnreadIcon from '@mui/icons-material/MarkEmailUnread';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';
import DOMPurify from 'dompurify'; // Add this package for sanitizing HTML
import { useLocation, useNavigate } from 'react-router-dom';

// Styled components for the message center layout - removed shadows and borders
const MessageListContainer = styled(Box)(({ theme }) => ({
  height: 'calc(100vh - 150px)',
  overflow: 'auto',
  padding: theme.spacing(1),
  position: 'relative',
}));

const MessageViewContainer = styled(Box)(({ theme }) => ({
  height: 'calc(100vh - 150px)',
  overflow: 'auto',
  padding: theme.spacing(3),
}));

const MessageItem = styled(ListItem)(({ theme, selected, unread }) => ({
  marginBottom: theme.spacing(1),
  borderRadius: theme.spacing(1),
  backgroundColor: selected ? theme.palette.action.selected : unread ? theme.palette.action.hover : 'inherit',
  '&:hover': {
    backgroundColor: theme.palette.action.hover,
  },
}));

const MessageContent = styled(Box)(({ theme }) => ({
  '& a': {
    color: theme.palette.primary.main,
    textDecoration: 'none',
    '&:hover': {
      textDecoration: 'underline',
    },
  },
}));

// Custom styled divider with spacing above and below
const StyledDivider = styled(Divider)(({ theme }) => ({
  marginTop: theme.spacing(1),
  marginBottom: theme.spacing(1),
}));

// Custom styled Typography for truncated titles
const TruncatedTitle = styled(Typography)(({ theme }) => ({
  fontWeight: 'inherit',
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
  width: '100%',
  display: 'block',
  maxWidth: '100%'
}));

// Styled pagination container to prevent wrapping
const PaginationContainer = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'center',
  marginTop: theme.spacing(2),
  overflow: 'auto',
  '& .MuiPagination-ul': {
    flexWrap: 'nowrap'
  },
  // Reduce spacing between pagination buttons on desktop
  [theme.breakpoints.up('md')]: {
    '& .MuiPaginationItem-root': {
      margin: '0 0px', // Reduce margin between buttons (default is 0 8px)
      padding: '0 1px', // Reduce padding inside buttons (default is 0 12px)
    }
  }
}));

// Function to strip HTML tags for preview text
const stripHtml = (html) => {
  const doc = new DOMParser().parseFromString(html, 'text/html');
  return doc.body.textContent || '';
};

// Function to format date in a readable format
const formatDate = (dateString) => {
  try {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    return dateString;
  }
};

// Function to format relative time for message list
const formatRelativeTime = (dateString) => {
  try {
    const date = new Date(dateString);
    return formatDistanceToNow(date, { addSuffix: true });
  } catch (error) {
    return dateString;
  }
};

// Custom message list skeleton that mimics the message list layout
const MessageListSkeleton = () => {
  return (
    <Box>
      {[1, 2, 3, 4, 5].map((item) => (
        <Box key={item} sx={{ mb: 1.5, px: 2 }}>
          <Skeleton variant="text" width="80%" height={28} />
          <Skeleton variant="text" width="40%" height={20} />
          <Skeleton variant="text" width="60%" height={20} />
          <Divider sx={{ mt: 2.5, mb: 2 }} />
        </Box>
      ))}
    </Box>
  );
};

// Helper function to get query parameters from URL
const useQuery = () => {
  return new URLSearchParams(useLocation().search);
};

const Messages = ({ title }) => {
  const userInfo = useSelector((state) => state.auth.userInfo);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const query = useQuery();
  const navigate = useNavigate();
  const messageIdFromUrl = query.get('messageId');

  const [messages, setMessages] = useState([]);
  const [selectedMessage, setSelectedMessage] = useState(null);
  const [loading, setLoading] = useState(false);
  const [messageLoading, setMessageLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [error, setError] = useState(null);
  const [showMessageView, setShowMessageView] = useState(false);
  const [initialLoadComplete, setInitialLoadComplete] = useState(false);
  const pageSize = 5;

  // Handle initial load with messageId in URL
  useEffect(() => {
    const handleMessageIdInUrl = async () => {
      if (messageIdFromUrl) {
        try {
          // First, find which page this message is on
          const pageResponse = await axiosPrivate.get(`/message/messagePage?messageId=${messageIdFromUrl}&size=${pageSize}`);

          if (pageResponse.data.errCode === "0" && pageResponse.data.payload && pageResponse.data.payload.page !== undefined) {
            const messagePage = pageResponse.data.payload.page;

            // Set the page and wait for the messages to load
            setPage(messagePage);

            // We'll let the page change trigger the message list loading
            // Then the useEffect below will handle loading the specific message
          } else {
            throw new Error("Failed to find message page");
          }
        } catch (err) {
          console.error("Error finding message page:", err);
          setError("Failed to locate the specified message. Please try again later.");
          // Still load the first page of messages
          fetchMessages();
        }
      } else {
        // No messageId in URL, just load the first page
        fetchMessages();
      }
    };

    handleMessageIdInUrl();
  }, [messageIdFromUrl]);

  // Fetch message list when page changes
  useEffect(() => {
    fetchMessages();
  }, [page]);

  // Handle loading specific message after messages are loaded
  useEffect(() => {
    const loadSpecificMessage = async () => {
      if (messageIdFromUrl && messages.length > 0 && !initialLoadComplete) {
        // Check if the message is in the current page
        const messageInCurrentPage = messages.find(msg => msg.id === messageIdFromUrl);

        if (messageInCurrentPage) {
          // Message is in the current page, select it
          fetchMessageDetails(messageIdFromUrl);
          setInitialLoadComplete(true);

          // Remove the messageId from URL to avoid reloading on refresh
          navigate('/messages', { replace: true });
        }
      }
    };

    loadSpecificMessage();
  }, [messages, messageIdFromUrl, initialLoadComplete]);

  // This useEffect handles modifying all links to open in new tabs
  useEffect(() => {
    if (selectedMessage) {
      // Use a small timeout to ensure the DOM is fully rendered
      setTimeout(() => {
        const messageContentElements = document.querySelectorAll('.message-content a');
        messageContentElements.forEach(link => {
          link.setAttribute('target', '_blank');
          link.setAttribute('rel', 'noopener noreferrer');
        });
      }, 100);
    }
  }, [selectedMessage]);

  const fetchMessages = async () => {
    setLoading(true);
    if (!messageIdFromUrl) {
      setSelectedMessage(null);
    }

    try {
      const response = await axiosPrivate.get(`/message/list?page=${page}&size=${pageSize}`);
      const data = response.data;

      if (data.errCode === "0" && data.payload && data.payload.messages) {
        setMessages(data.payload.messages);

        // Use the totalPages value from the API response
        if (data.payload.totalPages !== undefined) {
          setTotalPages(data.payload.totalPages);
        } else {
          // Fallback calculation if totalPages is not provided
          const totalMessages = data.payload.totalMessages || data.payload.messages.length;
          setTotalPages(Math.ceil(totalMessages / pageSize));
        }
      } else {
        throw new Error("Failed to fetch messages");
      }
    } catch (err) {
      setError("Failed to load messages. Please try again later.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchMessageDetails = async (messageId) => {
    // For mobile view, immediately show the message view with loading state
    if (isMobile) {
      setShowMessageView(true);
      setMessageLoading(true);
    } else {
      setMessageLoading(true);
    }

    try {
      const response = await axiosPrivate.get(`/message/single?messageId=${messageId}`);
      const data = response.data;

      if (data.errCode === "0" && data.payload && data.payload.message) {
        setSelectedMessage(data.payload.message);
        // Mark message as read if it's not already read
        if (!data.payload.message.read) {
          markMessageAsRead(messageId, data.payload.message);
        }
      } else {
        throw new Error("Failed to fetch message details");
      }
    } catch (err) {
      setError("Failed to load message details. Please try again later.");
      console.error(err);

      // For mobile view, go back to message list if there's an error
      if (isMobile) {
        setShowMessageView(false);
      }
    } finally {
      setMessageLoading(false);
    }
  };

  const markMessageAsRead = async (messageId, messageObj = null) => {
    try {
      const response = await axiosPrivate.get(`/message/readStatus?messageId=${messageId}&isRead=true`);
      const data = response.data;

      if (data.errCode === "0") {
        // Update the local state to mark the message as read in the list
        setMessages(messages.map(message =>
          message.id === messageId ? { ...message, read: true } : message
        ));

        // Also update the selected message if it's the one being marked as read
        if (selectedMessage && selectedMessage.id === messageId) {
          setSelectedMessage(prev => ({ ...prev, read: true }));
        } else if (messageObj) {
          // If we have a message object (from fetchMessageDetails), update it
          setSelectedMessage({ ...messageObj, read: true });
        }
      }
    } catch (err) {
      console.error("Failed to mark message as read:", err);
    }
  };

  const toggleMessageReadStatus = async (e, messageId, currentReadStatus) => {
    e.stopPropagation(); // Prevent message selection when clicking the read/unread button

    try {
      const newReadStatus = !currentReadStatus;
      const response = await axiosPrivate.get(`/message/readStatus?messageId=${messageId}&isRead=${newReadStatus}`);
      const data = response.data;

      if (data.errCode === "0") {
        // Update the local state to toggle the message's read status
        setMessages(messages.map(message =>
          message.id === messageId ? { ...message, read: newReadStatus } : message
        ));

        // If the selected message's status is being changed, update that too
        if (selectedMessage && selectedMessage.id === messageId) {
          setSelectedMessage({ ...selectedMessage, read: newReadStatus });
        }
      }
    } catch (err) {
      setError("Failed to update message status. Please try again later.");
      console.error(err);
    }
  };

  const handleSelectMessage = (messageId) => {
    if (selectedMessage?.id === messageId) return;
    fetchMessageDetails(messageId);
  };

  const handlePageChange = (event, value) => {
    setPage(value - 1); // MUI Pagination is 1-based, but our API is 0-based
  };

  const clearSelectedMessage = () => {
    setSelectedMessage(null);
    if (isMobile) {
      setShowMessageView(false);
    }
  };

  const handleCloseError = () => {
    setError(null);
  };

  // Sanitize HTML content to prevent XSS attacks and ensure links open in new tabs
  const sanitizeHtml = (html) => {
    // Configure DOMPurify to allow target and rel attributes
    const sanitizedHtml = DOMPurify.sanitize(html, {
      ADD_ATTR: ['target', 'rel']
    });

    // Add class to help target all links for adding target="_blank"
    const parser = new DOMParser();
    const doc = parser.parseFromString(sanitizedHtml, 'text/html');

    // Modify all links to open in new tabs
    const links = doc.querySelectorAll('a');
    links.forEach(link => {
      link.setAttribute('target', '_blank');
      link.setAttribute('rel', 'noopener noreferrer');
    });

    // Add a class to the content container to help with targeting elements
    const contentContainer = doc.body;
    contentContainer.classList.add('message-content');

    return { __html: contentContainer.innerHTML };
  };

  return (
    <HelmetComponent title={"Messages"}>
      <div className='GenericPage'>
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">Messages</Typography>
                </Box>
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_right" sx={{ 'flex-grow': '1' }}>
                  <Box className="GenericPage__container_title_flexBox_right">
                    <Button variant="contained" size="medium" onClick={() => { fetchMessages(); setPage(0); }}>Refresh</Button>
                  </Box>
                </Box>
              </Box>
            </Container>
            <Container maxWidth="lg">
              <Paper className='GenericPage__container_paper' variant='outlined'>
                <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
                  <Grid container spacing={2}>
                    {/* Message List - show only if not in mobile view with message details */}
                    {(!isMobile || (isMobile && !showMessageView)) && (
                      <Grid item xs={12} md={3.6}>
                        <MessageListContainer>
                          {loading ? (
                            // Use MessageListSkeleton for message list loading
                            <MessageListSkeleton />
                          ) : messages.length > 0 ? (
                            <>
                              <List>
                                {messages.map((message) => (
                                  <React.Fragment key={message.id}>
                                    <MessageItem
                                      button
                                      selected={selectedMessage?.id === message.id}
                                      unread={!message.read}
                                      onClick={() => handleSelectMessage(message.id)}
                                    >
                                      <Box sx={{ width: 'calc(100% - 40px)', overflow: 'hidden' }}>
                                        <Box display="flex" alignItems="center" sx={{ width: '100%' }}>
                                          {/* Unread indicator dot at the beginning */}
                                          {!message.read && (
                                            <FiberManualRecordIcon
                                              color="primary"
                                              sx={{
                                                fontSize: '10px',
                                                mr: 1,
                                                flexShrink: 0
                                              }}
                                            />
                                          )}
                                          <TruncatedTitle
                                            variant="subtitle1"
                                            sx={{ fontWeight: message.read ? 'normal' : 'bold' }}
                                          >
                                            {message.title}
                                          </TruncatedTitle>
                                        </Box>
                                        {/* Using relative time format for message list */}
                                        <Typography variant="body2" color="text.secondary">
                                          {formatRelativeTime(message.sentTimeStamp)}
                                        </Typography>
                                        {/* Message content preview - now using the contentPreview field from API */}
                                        <Typography
                                          variant="body2"
                                          color="text.secondary"
                                          sx={{
                                            display: '-webkit-box',
                                            WebkitLineClamp: 2,
                                            WebkitBoxOrient: 'vertical',
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            mt: 0.5
                                          }}
                                        >
                                          {message.contentPreview}
                                        </Typography>
                                      </Box>
                                      <Tooltip title={message.read ? "Mark as unread" : "Mark as read"} arrow>
                                        <IconButton
                                          size="small"
                                          onClick={(e) => toggleMessageReadStatus(e, message.id, message.read)}
                                          aria-label={message.read ? "Mark as unread" : "Mark as read"}
                                          sx={{ ml: 'auto' }}
                                        >
                                          {message.read ? <MarkEmailUnreadIcon fontSize="small" /> : <MarkEmailReadIcon fontSize="small" />}
                                        </IconButton>
                                      </Tooltip>
                                    </MessageItem>
                                    {/* Use StyledDivider instead of regular Divider */}
                                    <StyledDivider component="li" />
                                  </React.Fragment>
                                ))}
                              </List>
                              {totalPages > 1 && (
                                <PaginationContainer>
                                  <Pagination
                                    count={totalPages}
                                    page={page + 1}
                                    onChange={handlePageChange}
                                    color="primary"
                                    size={isMobile ? "small" : "medium"}
                                    siblingCount={isMobile ? 1 : 1}
                                  />
                                </PaginationContainer>
                              )}
                            </>
                          ) : (
                            <Box display="flex" justifyContent="center" alignItems="center" height="100%">
                              <Typography variant="body1" color="text.secondary">
                                Yay, empty inbox. Enjoy your day!
                              </Typography>
                            </Box>
                          )}
                        </MessageListContainer>
                      </Grid>
                    )}

                    {/* Divider between message list and content - only show on desktop */}
                    {!isMobile && (
                      <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
                    )}

                    {/* Message View - show only if not in mobile view or if in mobile view with message selected */}
                    {(!isMobile || (isMobile && showMessageView)) && (
                      <Grid item xs={12} md={8.4 - (isMobile ? 0 : 0.5)}>
                        <MessageViewContainer>
                          {messageLoading ? (
                            // Use CircularProgress for message content loading
                            <Box display="flex" justifyContent="center" alignItems="center" height="100%">
                              <CircularProgress />
                            </Box>
                          ) : selectedMessage ? (
                            <Box>
                              <Box display="flex" alignItems="center" mb={3}>
                                {/* Only show back button on mobile */}
                                {isMobile && (
                                  <IconButton
                                    onClick={clearSelectedMessage}
                                    sx={{ mr: 1 }}
                                    aria-label="Back to list"
                                  >
                                    <ArrowBackIcon />
                                  </IconButton>
                                )}
                                <Typography variant="h5" component="h2">
                                  {selectedMessage.title}
                                </Typography>
                              </Box>

                              <Box mb={3} display="flex" justifyContent="space-between" alignItems="center" flexWrap="wrap">
                                {/* Using actual timestamp for message details */}
                                <Typography variant="body2" color="text.secondary">
                                  {formatDate(selectedMessage.sentTimeStamp)}
                                </Typography>
                                <Button
                                  variant="outlined"
                                  size="small"
                                  startIcon={selectedMessage.read ? <MarkEmailUnreadIcon /> : <MarkEmailReadIcon />}
                                  onClick={(e) => toggleMessageReadStatus(e, selectedMessage.id, selectedMessage.read)}
                                  sx={{ mt: { xs: 1, sm: 0 } }}
                                >
                                  {selectedMessage.read ? "Mark as unread" : "Mark as read"}
                                </Button>
                              </Box>

                              <Divider sx={{ mb: 3 }} />

                              <MessageContent
                                className="message-content"
                                dangerouslySetInnerHTML={sanitizeHtml(selectedMessage.content)}
                              />
                            </Box>
                          ) : (
                            <Box display="flex" justifyContent="center" alignItems="center" height="100%">
                              {!loading && (
                                <Typography variant="body1" color="text.secondary" align="center">
                                  {messages.length === 0 ? (
                                    <React.Fragment>
                                      You have not received any messages yet.<br />
                                      When you receive messages, they will appear here.
                                    </React.Fragment>
                                  ) : (
                                    "Select a message to view"
                                  )}
                                </Typography>
                              )}
                            </Box>
                          )}
                        </MessageViewContainer>
                      </Grid>
                    )}
                  </Grid>

                  {/* Error Snackbar */}
                  <Snackbar open={!!error} autoHideDuration={6000} onClose={handleCloseError}>
                    <Alert onClose={handleCloseError} severity="error" sx={{ width: '100%' }}>
                      {error}
                    </Alert>
                  </Snackbar>
                </Container>
              </Paper>
            </Container>
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent>
  );
}

export default Messages
