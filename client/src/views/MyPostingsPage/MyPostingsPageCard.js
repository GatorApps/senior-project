import * as React from 'react';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import { axiosPrivate } from '../../apis/backend';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Link from '@mui/material/Link';
import { Divider } from '@mui/material';

const PostingPageCard = () => {
    const [postings, setPostings] = useState({ openPositions: [], closedPositions: [], archivedPositions: [] });
    const [loading, setLoading] = useState(true);
    const [tabIndex, setTabIndex] = useState(0);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
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
    }, []);

    const handleTabChange = (event, newIndex) => {
        setTabIndex(newIndex);
    };

    const filteredPostings = tabIndex === 0 ? postings.openPositions : postings.closedPositions

    return (
        <Box sx={{ width: '100%' }}>
            <Card variant="outlined">
                <CardContent>
                    <Typography gutterBottom variant="h2" component="div" sx={{ lineHeight: 1.2, fontSize: '1.3125rem', fontWeight: 400 }}>
                        My Postings
                    </Typography>
                    <Box marginY="16px">
                        <Tabs value={tabIndex} onChange={handleTabChange} centered sx={{ marginBottom: '16px' }}>
                            <Tab sx={{ fontSize: '0.75rem' }} label="Open Positions" />
                            <Tab sx={{ fontSize: '0.75rem' }} label="Closed Positions" />
                            {/* <Tab sx={{ fontSize: '0.75rem' }} label="Archived Positions" /> */}
                        </Tabs>

                        {loading ? (
                            <SkeletonGroup boxPadding={'0'} />
                        ) : error ? (
                            <Typography color="error">{error}</Typography>
                        ) : filteredPostings.length === 0 ? (
                            <Typography variant="body1" color="textSecondary">No postings available.</Typography>
                        ) : (
                            filteredPostings.map((posting) => (
                                <Link href={`/postingEditor?postingId=${posting.positionId}`} target="_blank" style={{ textDecoration: 'none', color: "black" }}>
                                    <Box key={posting.positionId} sx={{
                                        px: 1,
                                        py: 1,
                                        cursor: 'pointer',
                                        borderRadius: 1,
                                        mb: 0.5,
                                        '&:hover': {
                                            bgcolor: 'action.hover',
                                        }
                                    }}>
                                        <Typography variant="h7" color="textSecondary">{posting.labName}</Typography>
                                        <Typography variant="h6" sx={{ fontWeight: 500 }}>{posting.name}</Typography>
                                        <Typography variant="body2" color="textSecondary">
                                            Last Updated: {posting.postedTimeStamp ? new Date(posting.postedTimeStamp).toLocaleString() : "N/A"}
                                        </Typography>

                                    </Box>
                                    <Divider sx={{ my: 0.5 }} />
                                </Link>
                            ))
                        )}
                    </Box>
                </CardContent>
                <CardActions>
                    <Button size="medium" onClick={() => navigate('/postingEditor')}>Post New</Button>
                    <Button size="medium" onClick={() => navigate('/postingManagement')}>View All</Button>
                </CardActions>
            </Card>
        </Box>
    );
}

export default PostingPageCard;
