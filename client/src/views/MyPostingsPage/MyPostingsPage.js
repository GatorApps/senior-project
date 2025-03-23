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

const PostingManagement = () => {
    const [postings, setPostings] = useState({ openPositions: [], closedPositions: [] });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [tabIndex, setTabIndex] = useState(0);
    const dispatch = useDispatch();
    const navigator = useNavigate();

    useEffect(() => {
        axiosPrivate.get('/posting/postingManagement')
            .then((response) => {
                console.log(response.data);
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
    }, []);

    const handleTabChange = (event, newIndex) => {
        setTabIndex(newIndex);
    };

    const filteredPostings = tabIndex === 0 ? postings.openPositions : postings.closedPositions;

    return (
        <HelmetComponent title={"Posting Management"}>
            <div className='PostingManagement'>
                <Header />
                <main>
                    <Container maxWidth="lg">
                        <Typography variant="h1" sx={{ marginBottom: 2, marginTop: 2 }}>Posting Management</Typography>

                        <Paper variant="outlined" sx={{ padding: '24px' }}>
                            <Tabs value={tabIndex} onChange={handleTabChange} centered>
                                <Tab label="Open Positions" />
                                <Tab label="Closed Positions" />
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
                                                variant="contained"
                                                onClick={() => navigator(`/postingEditor?postingId=${posting.positionId}`)}
                                            >Edit</Button>
                                            <Button variant="outlined">
                                                {posting.status.charAt(0).toUpperCase() + posting.status.slice(1)}
                                            </Button>
                                        </Box>
                                    </Box>
                                ))
                            )}
                        </Paper>
                    </Container>
                </main>
                <Footer />
            </div>
        </HelmetComponent>
    );
}

export default PostingManagement;
