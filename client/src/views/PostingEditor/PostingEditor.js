import React, { useState, useEffect } from 'react';
import { axiosPrivate } from '../../apis/backend';
import { useDispatch, useSelector } from 'react-redux';
import { useLocation } from 'react-router-dom';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import { FormControl, InputLabel, MenuItem, Select, TextField } from '@mui/material';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { useNavigate } from 'react-router-dom';
import CreateLabPopup from '../../components/CreateLabPopup/CreateLabPopup';

const PostingEditor = ({ title }) => {
  const userInfo = useSelector((state) => state.auth.userInfo);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [id, setId] = useState("");
  const [labId, setLabId] = useState("");
  const [name, setName] = useState("");
  const [rawDescription, setRawDescription] = useState("");
  const [supplementalQuestions, setSupplementalQuestions] = useState(null);
  const [status, setStatus] = useState("");

  const [labs, setLabs] = useState([]);

  const [open, setOpen] = useState(false);

  // Extract postingId from URL
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const postingId = searchParams.get('postingId');

  const navigate = useNavigate();

  const handleSave = async () => {
    try {
      const payload = { id, labId, name, description: rawDescription, supplementalQuestions, status };
      if (postingId) {
        await axiosPrivate.put('/posting/postingEditor', payload);
      } else {
        delete payload.id;
        await axiosPrivate.post('/posting/postingEditor', payload);
      }
      navigate('/postingManagement');
    } catch (err) {
      setError(err.message);
    }
  };

  const fetchExistingPosting = async () => {
    try {
      setLoading(true);
      const response = await axiosPrivate.get(`/posting/postingEditor?positionId=${postingId}`);
      setId(response.data.payload.position.id);
      setLabId(response.data.payload.position.labId);
      setName(response.data.payload.position.name);
      setRawDescription(response.data.payload.position.description);
      setSupplementalQuestions(response.data.payload.position.supplementalQuestions);
      setStatus(response.data.payload.position.status);
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchLabs = async () => {
    // get /lab/labsList
    try {
      const response = await axiosPrivate.get('/lab/labsList');
      setLabs(response.data.payload.labs);
    } catch (error) {
      setError(error.message || "Error fetching labs");
    }
  };

  useEffect(() => {
    if (postingId) fetchExistingPosting();
    fetchLabs();
  }, []);

  return (
    <HelmetComponent title={"Posting Editor"}>
      <div className='GenericPage'>
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">Page Title</Typography>
                </Box>
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_right" sx={{ 'flex-grow': '1' }}>
                  <Box className="GenericPage__container_title_flexBox_right">
                  </Box>
                </Box>
              </Box>
            </Container>
            <Container maxWidth="lg">
              <Paper variant='outlined' sx={{ padding: 2 }}>
                {loading ? (
                  <Typography>Loading...</Typography>
                ) : error ? (
                  <Typography color='error'>{error}</Typography>
                ) : (
                  <Box>
                    <FormControl fullWidth sx={{ marginBottom: 2 }}>
                      <Typography variant='h5' marginBottom={1}>Lab</Typography>
                      <Select
                        value={labId}
                        onChange={(e) => setLabId(e.target.value)}
                      >
                        {labs.length > 0 ?
                          labs.map((lab) => (
                            <MenuItem key={lab.labId} value={lab.labId}>{lab.labName}</MenuItem>
                          ))
                          : <MenuItem value=''>No labs found</MenuItem>
                        }
                      </Select>
                    </FormControl>

                    <Box sx={{ display: 'flex', justifyContent: 'end', alignItems: 'center', marginBottom: 2 }}>
                      {/* Left-aligned text */}
                      <Typography variant="body2" color="textSecondary" sx={{ marginRight: 2 }}>
                        Didn't see your lab? <strong>Create or edit existing</strong>
                      </Typography>
                      {/* Right-aligned button */}
                      <Button
                        // small and not as prominent as the main button
                        variant="outlined"
                        size="small"
                        onClick={() => setOpen(true)}  // Navigate to create post page
                      >
                        Manage Labs
                      </Button>
                    </Box>

                    <Typography variant='h5' marginBottom={1}>Title</Typography>
                    <TextField
                      fullWidth
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      sx={{ marginBottom: 4 }}
                    />

                    <Typography variant='h5' marginBottom={1} marginTop={2}>Description</Typography>
                    <Box sx={{
                      marginBottom: 4,
                      '.ql-editor': {
                        overflow: 'hidden',
                        resize: 'vertical',
                        minHeight: '40px'
                      }
                    }}>

                      <ReactQuill value={rawDescription} onChange={(value) => setRawDescription(value)}
                        sx={{ minHeight: '100px' }} />
                    </Box>

                    <Typography variant='h5' marginBottom={1} marginTop={2}>Supplemental Questions</Typography>
                    <Box sx={{
                      marginBottom: 4,
                      '.ql-editor': {
                        overflow: 'hidden',
                        resize: 'vertical',
                        minHeight: '40px'
                      }
                    }}>
                      <ReactQuill value={supplementalQuestions} onChange={(value) => setSupplementalQuestions(value)} />
                    </Box>



                    <Typography variant='h5' marginBottom={1} marginTop={2}>Status</Typography>
                    <Box sx={{ marginBottom: 4 }}>

                      <FormControl fullWidth sx={{ minWidth: 150 }}>
                        <Select
                          value={status}
                          onChange={(e) => setStatus(e.target.value)}
                        >
                          <MenuItem value="open">Open</MenuItem>
                          <MenuItem value="closed">Closed</MenuItem>
                          <MenuItem value="archived">Archived</MenuItem>
                        </Select>
                      </FormControl>
                    </Box>

                    <Box sx={{ display: 'flex', justifyContent: 'end', alignItems: 'center' }}>
                      <Box sx={{ flexGrow: 1 }} />
                      <Button
                        variant='contained'
                        onClick={handleSave}
                        color='primary'
                        sx={{ marginTop: 2, paddingX: 3, paddingY: 1 }}
                      >
                        Save
                      </Button>
                      <Button
                        // Red color, outlined, and small
                        variant="outlined"
                        onClick={() => navigate('/postingManagement')}
                        color="error"
                        sx={{ marginTop: 2, marginLeft: 2, paddingX: 3, paddingY: 1 }}
                      >
                        Cancel
                      </Button>
                    </Box>

                  </Box>
                )}
              </Paper>

              <CreateLabPopup
                open={open}
                onClose={() => setOpen(false)}
                labs={labs}
              />
            </Container>
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent>
  );
}

export default PostingEditor;