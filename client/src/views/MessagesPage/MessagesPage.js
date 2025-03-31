import React, { useEffect, useState } from 'react';
import { axiosPrivate } from '../../apis/backend';
import { useDispatch, useSelector } from 'react-redux';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import { CircularProgress, List, ListItem, ListItemText } from '@mui/material';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid2';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';

const MessagesPage = ({ title }) => {
  const userInfo = useSelector((state) => state.auth.userInfo);

  const [messages, setMessages] = useState([]);
  const [selectedMessage, setSelectedMessage] = useState(null);
  const [listIsLoading, setlistIsLoading] = useState(true);
  const [messageIsLoading, setMessageIsLoading] = useState(false);

  useEffect(() => {
    fetchMessages();
  }, []);

  const fetchMessages = async () => {
    setlistIsLoading(true);
    try {
      const response = await axiosPrivate.get("/message/list?page=0&size=10");
      if (response.data.errCode === "0") {
        setMessages(response.data.payload.messages);
      }
    } catch (error) {
      console.error("Error fetching messages", error);
    }
    setlistIsLoading(false);
  };

  const fetchMessageDetails = async (messageId) => {
    setMessageIsLoading(true);
    try {
      const response = await axiosPrivate.get(`/message/single?messageId=${messageId}`);
      if (response.data.errCode === "0") {
        setSelectedMessage(response.data.payload.message);
        markAsRead(messageId);
      }
    } catch (error) {
      console.error("Error fetching message details", error);
    }
    setMessageIsLoading(false);
  };

  const markAsRead = async (messageId) => {
    try {
      await axiosPrivate.get(`/message/readStatus?messageId=${messageId}&isRead=true`);
      setMessages((prevMessages) => prevMessages.map((msg) => (msg.id === messageId ? { ...msg, read: true } : msg)));
    } catch (error) {
      console.error("Error updating read status", error);
    }
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
                  {/* <Button size="medium" sx={{ 'margin-left': '16px' }}>Button</Button> */}
                </Box>
                {/* <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_right" sx={{ 'flex-grow': '1' }}>
                  <Box className="GenericPage__container_title_flexBox_right">
                    <Button variant="contained" size="medium">Button</Button>
                  </Box>
                </Box> */}
              </Box>
            </Container>
            <Container maxWidth="lg">
              <Paper className='GenericPage__container_paper' variant='outlined'>
                <Container>
                  <Grid container spacing={3}>
                    {/* Message List */}
                    <Grid item xs={4}>
                      <Paper>
                        {listIsLoading ? (
                          <SkeletonGroup boxPadding={'0'} />
                        ) : (
                          <List>
                            {messages.map((msg) => (
                              <ListItem
                                key={msg.id}
                                button
                                onClick={() => fetchMessageDetails(msg.id)}
                                style={{ backgroundColor: msg.read ? "#f0f0f0" : "#fff" }}
                              >
                                <ListItemText primary={msg.title} secondary={new Date(msg.sentTimeStamp).toLocaleString()} />
                              </ListItem>
                            ))}
                          </List>
                        )}
                      </Paper>
                    </Grid>

                    {/* Message Content */}
                    <Grid item xs={9}>
                      <Paper style={{ padding: "16px" }}>
                        {messageIsLoading ? (
                          <CircularProgress />
                        ) : selectedMessage ? (
                          <>
                            <Typography variant="h6">{selectedMessage.title}</Typography>
                            <Typography variant="body2" color="textSecondary">
                              {new Date(selectedMessage.sentTimeStamp).toLocaleString()}
                            </Typography>
                            <div dangerouslySetInnerHTML={{ __html: selectedMessage.content }} />
                          </>
                        ) : (
                          <Typography>Select a message to view details</Typography>
                        )}
                      </Paper>
                    </Grid>
                  </Grid>
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

export default MessagesPage;