import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { useLocation } from 'react-router-dom';
import { axiosPrivate } from '../../apis/backend';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import Link from '@mui/material/Link';
import Modal from '@mui/material/Modal';

const PostingDetailsPage = () => {
    const userInfo = useSelector((state) => state.auth.userInfo);

    // Extract postingId from URL
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const postingId = searchParams.get('postingId');

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [postingDetails, setPostingDetails] = useState(null);
    const [isSaved, setIsSaved] = useState(false);
    const [openApplyModal, setOpenApplyModal] = useState(false);

    // Fetch posting details
    useEffect(() => {
        axiosPrivate.get(`/posting?positionId=${postingId}`)
            .then((response) => {
                setPostingDetails(response.data.payload.position || {});
                setLoading(false);
            })
            .catch((error) => {
                setError(error);
                setLoading(false);
            });
    }, [postingId]);

    if (loading) {
        return <SkeletonGroup />;
    }

    if (error) {
        return <Typography color="error">Error loading data.</Typography>;
    }

    return (
        <HelmetComponent title={postingDetails.positionName || "Position Details"}>
            <div className='PostingDetailsPage'>
                <Header />
                <main>
                    <Box>
                        <Container maxWidth="lg">
                            <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                                {/* <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                                    <Typography variant="h1">Page Title</Typography>
                                    <Button size="medium" sx={{ 'margin-left': '16px' }}>Button</Button>
                                </Box>
                                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_right" sx={{ 'flex-grow': '1' }}>
                                    <Box className="GenericPage__container_title_flexBox_right">
                                        <Button variant="contained" size="medium">Button</Button>
                                    </Box>
                                </Box> */}
                            </Box>
                        </Container>
                        <Container maxWidth="lg">
                            <Paper className='PostingDetailsPage__container' variant='outlined' sx={{ padding: '24px' }}>
                                {/* Title and Dates */}
                                <Typography variant="h4">{postingDetails.positionName}</Typography>
                                <Typography variant="body2" color="textSecondary">
                                    Posted: {new Date(postingDetails.postedTimeStamp).toLocaleDateString()} | Last Updated: {new Date(postingDetails.postedTimeStamp).toLocaleDateString()}
                                </Typography>

                                {/* Lab Name */}
                                <Box sx={{ marginTop: '16px' }}>
                                    <Link href={`/lab?labId=${postingDetails.labId}`} target="_blank" underline="hover">
                                        <Typography variant="h6" color="primary">
                                            {postingDetails.labName}
                                        </Typography>
                                    </Link>
                                </Box>

                                {/* Job Description */}
                                <Box sx={{ marginTop: '16px' }}>
                                    <Typography variant="h6">About the Job</Typography>
                                    <Box dangerouslySetInnerHTML={{ __html: postingDetails.positionDescription }} />
                                </Box>

                                {/* Actions - Apply & Save */}
                                <Box sx={{ marginTop: '24px', display: 'flex', gap: '12px' }}>
                                    <Button variant="contained" color="primary" onClick={() => setOpenApplyModal(true)}>
                                        Apply
                                    </Button>

                                    <IconButton onClick={() => setIsSaved(!isSaved)} color={isSaved ? "secondary" : "default"}>
                                        {isSaved ? <FavoriteIcon /> : <FavoriteBorderIcon />}
                                    </IconButton>
                                </Box>
                            </Paper>
                        </Container>
                    </Box>
                </main>

                <Footer />

                {/* Apply Modal */}
                <Modal open={openApplyModal} onClose={() => setOpenApplyModal(false)}>
                    <Box sx={{
                        position: 'absolute',
                        top: '50%',
                        left: '50%',
                        transform: 'translate(-50%, -50%)',
                        width: 400,
                        bgcolor: 'background.paper',
                        boxShadow: 24,
                        p: 4,
                        borderRadius: '8px'
                    }}>
                        <Typography variant="h6">Apply for {postingDetails.positionName}</Typography>
                        <Typography variant="body2" sx={{ marginTop: '8px' }}>
                            Application form will be available soon.
                        </Typography>
                        <Button onClick={() => setOpenApplyModal(false)} variant="contained" sx={{ marginTop: '16px' }}>
                            Close
                        </Button>
                    </Box>
                </Modal>
            </div >
        </HelmetComponent >
    );
}

export default PostingDetailsPage;
