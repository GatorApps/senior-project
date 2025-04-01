import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import { axiosPrivate } from '../../apis/backend';
import { useNavigate } from 'react-router-dom';
import { FormControl, InputLabel } from '@mui/material';

import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';

const PostingManagement = () => {
    const [postings, setPostings] = useState({ openPositions: [], closedPositions: [], archivedPositions: [] });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [tabIndex, setTabIndex] = useState(0);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const fetchPostings = async () => {
        axiosPrivate.get('/posting/postingManagement')
            .then((response) => {
                // console.log(response.data);
                if (response.data && response.data.errCode === "0" && response.data.payload && response.data.payload.postingsList) {
                    setPostings(response.data.payload.postingsList);
                } else {
                    setError("No postings found");
                }
                setLoading(false);
            })
            .catch((error) => {
                setError(error.message || "Error fetching postings");
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchPostings();
    }, []);

    const handleTabChange = (event, newIndex) => {
        setTabIndex(newIndex);
    };

    const filteredPostings = tabIndex === 0 ? postings.openPositions : tabIndex === 1 ? postings.closedPositions : postings.archivedPositions;

    const updatePositionStatus = async (posting, newStatus) => {
        try {
            // /posting/postingStatus?positionId=67ddf24344c53033691a7908&status=open
            await axiosPrivate.put(`/posting/postingStatus`, null, {
                params: {
                    positionId: posting.positionId,
                    status: newStatus
                }
            });
            fetchPostings();
        } catch (error) {
            setError("Error updating status");
        }
    };

    return (
        <HelmetComponent title={"Posting Management"}>
            <div className='PostingManagement'>
                <Header />
                <main>
                    <Box>
                        <Container maxWidth="lg">
                            <Box
                                className="GenericPage__container_title_box GenericPage__container_title_flexBox"
                                sx={{
                                    display: 'flex',  // Use flexbox
                                    justifyContent: 'space-between',  // Distribute space between the title and button
                                    alignItems: 'center',  // Vertically align the title and button
                                }}
                            >
                                <Typography variant='h4' gutterBottom>Posting Management</Typography>
                                <Button
                                    variant="contained"
                                    size="medium"
                                    onClick={() => navigate('/postingEditor')}  // Navigate to create post page
                                >
                                    Add New Post
                                </Button>
                            </Box>
                        </Container>

                        <Container maxWidth="lg">
                            <Paper variant="outlined" sx={{ padding: '24px' }}>
                                <Tabs value={tabIndex} onChange={handleTabChange} centered>
                                    <Tab label="Open Positions" />
                                    <Tab label="Closed Positions" />
                                    <Tab label="Archived Positions" />
                                </Tabs>

                                {loading ? (
                                    <SkeletonGroup boxPadding={'0'} />
                                ) : error ? (
                                    <Typography color="error">{error}</Typography>
                                ) : filteredPostings.length === 0 ? (
                                    <Typography variant="body1" color="textSecondary">No postings available.</Typography>
                                ) : (
                                    filteredPostings.map((posting) => (
                                        <Box key={posting.positionId} sx={{ marginBottom: '16px', padding: '16px', borderBottom: '1px solid #ddd' }}>
                                            <Typography variant="h6" color="textSecondary">{posting.labName}</Typography>
                                            <Typography variant="h5" sx={{ fontWeight: 500 }}>{posting.name}</Typography>
                                            <Typography variant="body2" color="textSecondary">
                                                Last Updated: {posting.postedTimeStamp ? new Date(posting.postedTimeStamp).toLocaleString() : "N/A"}
                                            </Typography>
                                            <Box sx={{ display: 'flex', gap: 2, marginTop: 1 }}>
                                                <Button
                                                    variant="outlined"
                                                    onClick={() => navigate(`/postingEditor?postingId=${posting.positionId}`)}
                                                >Edit</Button>
                                                <FormControl sx={{ minWidth: 150 }}>
                                                    <Select
                                                        value={posting.status}
                                                        onChange={(e) => updatePositionStatus(posting, e.target.value)}
                                                    >
                                                        <MenuItem value="open">Open</MenuItem>
                                                        <MenuItem value="closed">Closed</MenuItem>
                                                        <MenuItem value="archived">Archived</MenuItem>
                                                    </Select>
                                                </FormControl>
                                            </Box>
                                        </Box>
                                    ))
                                )}
                            </Paper>
                        </Container>
                    </Box>
                </main>
                <Footer />
            </div>
        </HelmetComponent>
    );
}

export default PostingManagement;
