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

  // Function to strip HTML tags for preview text
  const stripHtml = (html) => {
    const doc = new DOMParser().parseFromString(html, 'text/html');
    return doc.body.textContent || '';
  };

  // Get a preview of the message content (first 60 characters)
  const getContentPreview = (content) => {
    const plainText = stripHtml(content);
    return plainText.length > 60 ? plainText.substring(0, 60) + '...' : plainText;
  };

  // Format relative time (e.g., "2 days ago")
  const formatRelativeTime = (dateString) => {
    try {
      const date = new Date(dateString);
      return formatDistanceToNow(date, { addSuffix: true });
    } catch (error) {
      return dateString;
    }
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
              <SkeletonGroup boxPadding={'0'} />
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
                      onClick={() => navigate(`/messages`)}
                    >
                      <Box sx={{ display: 'flex', alignItems: 'flex-start', width: '100%' }}>
                        {!message.read && (
                          <FiberManualRecordIcon
                            color="primary"
                            sx={{
                              fontSize: '10px',
                              mr: 1,
                              mt: 0.8,
                              flexShrink: 0
                            }}
                          />
                        )}
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
                            {getContentPreview(message.content)}
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
              <Typography variant="body2" sx={{ fontSize: '0.938rem' }}>
                You have no messages. Messages related to your applications and postings will appear here.
              </Typography>
            )}
          </Box>
        </CardContent>
        <CardActions>
          <Button size="medium" onClick={() => navigate('/messages')}>
            View All
          </Button>
        </CardActions>
      </Card>
    </Box>
  );
}

export default MessagesCard;