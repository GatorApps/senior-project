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
import ApplicationPopup from '../../components/ApplicationPopup/ApplicationPopup';

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
    const [applicationQuestions, setApplicationQuestions] = useState([]);
    const [alreadyApplied, setAlreadyApplied] = useState(false); // New state for application status

    const checkAlreadyApplied = async () => {
        try {
            const response = await axiosPrivate.get(`/application/alreadyApplied?positionId=${postingId}`);
            if (response.data.errCode === "0") {
                setAlreadyApplied(response.data.payload.alreadyApplied);
            }
        } catch (error) {
            console.error("Error checking application status:", error);
        }
    };

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

        // Fetch application questions
        axiosPrivate.get(`posting/supplementalQuestions?positionId=${postingId}`)
            .then((response) => {
                // console.log(response.data.payload.position.applicationQuestions);
                setApplicationQuestions(response.data.payload.position.applicationQuestions);
            })
            .catch((error) => {
                setError(error);
                setLoading(false);
            });

        // Check if the user has already applied
        checkAlreadyApplied();

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
                    <Box sx={{ padding: '24px', backgroundColor: '#f5f5f5' }}>
                        <Container maxWidth="lg">
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4 }}>
                                <Box>
                                    {/* <Typography variant="h5" component="h2" sx={{ marginBottom: 1 }}>
                                        Position Details
                                    </Typography> */}
                                    <Typography variant="subtitle1" color="textSecondary">
                                        View details about the position here.
                                    </Typography>
                                </Box>
                            </Box>
                        </Container>
                        <Container maxWidth="lg">
                            <Paper className='PostingDetailsPage__container' variant='outlined' sx={{ padding: '24px', marginBottom: 4 }}>
                                {/* Title and Dates */}
                                <Typography variant="h4" sx={{ marginBottom: 2 }}>{postingDetails.positionName}</Typography>
                                <Typography variant="body2" color="textSecondary" sx={{ marginBottom: 4 }}>
                                    Posted: {new Date(postingDetails.postedTimeStamp).toLocaleDateString()} | Last Updated: {new Date(postingDetails.postedTimeStamp).toLocaleDateString()}
                                </Typography>

                                {/* Lab Name */}
                                <Box sx={{ marginTop: '16px', marginBottom: 4 }}>
                                    <Link href={`/lab?labId=${postingDetails.labId}`} target="_blank" underline="hover">
                                        <Typography variant="h6" color="primary">
                                            {postingDetails.labName}
                                        </Typography>
                                    </Link>
                                </Box>

                                {/* Job Description */}
                                <Box sx={{ marginTop: '16px', marginBottom: 4 }}>
                                    <Typography variant="h6" sx={{ marginBottom: 2 }}>About the Position</Typography>
                                    <Box dangerouslySetInnerHTML={{ __html: postingDetails.positionDescription }} />
                                </Box>

                                {/* Actions - Apply & Save */}
                                <Box sx={{ marginTop: '24px', display: 'flex', gap: '16px', alignItems: 'center' }}>
                                    {!alreadyApplied ? (
                                        <Button variant="contained" color="primary" onClick={() => setOpenApplyModal(true)}>
                                            Apply
                                        </Button>
                                    ) : (
                                        <Typography color="success">You have already applied for this position.</Typography>
                                    )}

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
                <ApplicationPopup
                    open={openApplyModal}
                    questions={applicationQuestions}
                    onClose={() => {
                        setOpenApplyModal(false);
                        checkAlreadyApplied();
                    }}
                    postingId={postingId}
                    labId={postingDetails.labId}
                    positionName={postingDetails.positionName}
                    labName={postingDetails.labName}
                    userInfo={userInfo}
                />
            </div>
        </HelmetComponent>

    );
}

export default PostingDetailsPage;
