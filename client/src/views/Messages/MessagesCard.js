import * as React from 'react';
import { useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import { useNavigate } from 'react-router-dom';
import { axiosPrivate } from '../../apis/backend';
import { formatDistanceToNow } from 'date-fns';
import FiberManualRecordIcon from '@mui/icons-material/FiberManualRecord';
import { Divider, List, ListItem } from '@mui/material';

const MessagesCard = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [messages, setMessages] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchRecentMessages = async () => {
      try {
        // Fetch the most recent messages (first page, small page size)
        const response = await axiosPrivate.get('/message/list?page=0&size=3');
        const data = response.data;

        if (data.errCode === "0" && data.payload && data.payload.messages) {
          setMessages(data.payload.messages);
        } else {
          throw new Error("Failed to fetch messages");
        }
      } catch (err) {
        console.error("Error fetching messages:", err);
        setError("Unable to load messages");
      } finally {
        setLoading(false);
      }
    };

    fetchRecentMessages();
  }, []);

  // Format relative time (e.g., "2 days ago")
  const formatRelativeTime = (dateString) => {
    try {
      const date = new Date(dateString);
      return formatDistanceToNow(date, { addSuffix: true });
    } catch (error) {
      return dateString;
    }
  };

  // Navigate to specific message with messageId query parameter
  const navigateToMessage = (messageId) => {
    navigate(`/messages?messageId=${messageId}`);
  };

  return (
    <Box sx={{ width: '100%' }}>
      <Card variant="outlined">
        <CardContent>
          <Typography gutterBottom variant="h2" component="div" sx={{ lineHeight: 1.2, fontSize: '1.3125rem', fontWeight: 400 }}>
            Messages
          </Typography>
          <Box marginY="16px">
            {loading ? (
              <>
                <SkeletonGroup boxPadding={'0'} />
                <Divider sx={{ my: 1.44 }} />
                <SkeletonGroup boxPadding={'0'} />
                <Divider sx={{ my: 1.44 }} />
                <SkeletonGroup boxPadding={'0'} />
              </>
            ) : error ? (
              <Typography variant="body2" color="error" sx={{ fontSize: '0.938rem' }}>
                {error}
              </Typography>
            ) : messages.length > 0 ? (
              <List sx={{ p: 0 }}>
                {messages.slice(0, 3).map((message, index) => (
                  <React.Fragment key={message.id}>
                    <ListItem
                      sx={{
                        px: 1.5,
                        py: 1,
                        cursor: 'pointer',
                        borderRadius: 1,
                        mb: 0.5,
                        '&:hover': {
                          bgcolor: 'action.hover',
                        }
                      }}
                      onClick={() => navigateToMessage(message.id)}
                    >
                      <Box sx={{ display: 'flex', width: '100%' }}>
                        {/* Unread indicator dot at the beginning - with consistent vertical alignment */}
                        {!message.read ? (
                          <Box sx={{
                            display: 'flex',
                            flexDirection: 'column',
                            mr: 1,
                            mt: 0.5,
                            alignSelf: 'flex-start',
                            flexShrink: 0
                          }}>
                            <FiberManualRecordIcon
                              color="primary"
                              sx={{
                                fontSize: '10px',
                              }}
                            />
                          </Box>
                        ) : (
                          // Empty box to maintain consistent spacing when no dot
                          <Box sx={{ width: 18, flexShrink: 0 }} />
                        )}

                        {/* Content container - all content is indented consistently */}
                        <Box sx={{ width: '100%', overflow: 'hidden' }}>
                          <Typography
                            variant="body2"
                            sx={{
                              fontWeight: message.read ? 'normal' : 'bold',
                              whiteSpace: 'nowrap',
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                              fontSize: '0.938rem'
                            }}
                          >
                            {message.title}
                          </Typography>

                          {/* Using contentPreview from API */}
                          <Typography
                            variant="body2"
                            color="text.secondary"
                            sx={{
                              fontSize: '0.813rem',
                              display: '-webkit-box',
                              WebkitLineClamp: 1,
                              WebkitBoxOrient: 'vertical',
                              overflow: 'hidden'
                            }}
                          >
                            {message.contentPreview}
                          </Typography>

                          <Typography
                            variant="caption"
                            color="text.secondary"
                            sx={{ fontSize: '0.75rem' }}
                          >
                            {formatRelativeTime(message.sentTimeStamp)}
                          </Typography>
                        </Box>
                      </Box>
                    </ListItem>
                    {index < messages.length - 1 && (
                      <Divider sx={{ my: 0.5 }} />
                    )}
                  </React.Fragment>
                ))}
              </List>
            ) : (
              <Box>
                <Typography
                  variant="body1"
                  color="textSecondary"
                  sx={{
                    textAlign: "center",
                    py: 2,
                  }}
                >
                  Yay, empty inbox. Enjoy your day!
                </Typography>
                <Typography
                  variant="body2"
                  color="textSecondary"
                  sx={{
                    textAlign: "left",
                    py: 2,
                  }}
                >
                  You have not received any messages yet. When you receive messages, they will appear here.
                </Typography>
              </Box>
            )}
          </Box>
        </CardContent>
        <CardActions>
          <Button size="medium" onClick={() => navigate('/messages')}>
            View more in Message Center
          </Button>
        </CardActions>
      </Card>
    </Box>
  );
}

export default MessagesCard
