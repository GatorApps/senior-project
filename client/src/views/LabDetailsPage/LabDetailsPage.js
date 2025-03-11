import React from 'react';
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
import Link from '@mui/material/Link';
import IconButton from '@mui/material/IconButton';
import StarBorderIcon from '@mui/icons-material/StarBorder';

// // example schema for labDetails
// const response = {
//     "errCode": "0",
//     "payload": {
//         "lab": {
//             "labId": "6797d2ff9ecab28bd5548672",
//             "labName": "Ava's Testing Lab",
//             "labDescription": "Testing description",
//             "department": "Department of Mongo Testing",
//             "website": "https://testlab.gatorapps.org",
//             "email": "testlab@gatorapps.org",
//             "positions": [
//                 {
//                     "positionName": "Ava's Testing Position",
//                     "positionDescription": "test position",
//                     "postedTimeStamp": "2025-01-10T19:27:09.573+00:00",
//                     "positionId": "6797d2a79ecab28bd554866b"
//                 },
//                 {
//                     "positionName": "Ava's Testing Position biology 2",
//                     "positionDescription": "test position chemistry",
//                     "postedTimeStamp": "2025-05-10T19:27:09.573+00:00",
//                     "positionId": "67be52b8d7565c4e30236218"
//                 },
//                 {
//                     "positionName": "Ava's Testing Position electrical 3",
//                     "positionDescription": "<p><strong>Description</strong><br>This is a lab posting for students interested in mechanical, robots, hardware, ai. We work with path-finding algorithms.&nbsp;<br><br><strong>Preferred Qualification</strong><br>3.0 GPA<br>Sophomore status</p>",
//                     "postedTimeStamp": "2025-03-02T02:19:05.806+00:00",
//                     "positionId": "67c3c01ab87e185493ae9c10"
//                 }
//             ]
//         }
//     }
// }

const GenericPage = ({ title }) => {
    const userInfo = useSelector((state) => state.auth.userInfo);
    const location = useLocation();

    // Extract the labid parameter from the URL
    const searchParams = new URLSearchParams(location.search);
    const labid = searchParams.get('labId');


    // Set loading state
    const [loading, setLoading] = React.useState(true);
    const [labDetails, setLabDetails] = React.useState(null);
    const [error, setError] = React.useState(null);
    const dispatch = useDispatch();

    React.useEffect(() => {
        // Fetch from /lab?labid={labid}
        console.log(labid);
        axiosPrivate.get(`/lab?labId=${labid}`)
            .then((response) => {
                setLabDetails(response.data.payload);
                setLoading(false);
            })
            .catch((error) => {
                setError(error);
                setLoading(false);
            });
    }, [labid]);


    return (
        <HelmetComponent title={"Lab Details"}>
            <div className='GenericPage'>
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
                            <Paper
                                className='GenericPage__container_paper'
                                variant='outlined'
                                sx={{ maxWidth: 1000, mx: "auto", p: 4, bgcolor: "background.paper" }}
                            >
                                {loading && <SkeletonGroup />}
                                {!loading && labDetails && (
                                    <Box>
                                        <Typography variant="h2" sx={{ fontSize: "2rem", fontWeight: "bold", mb: 2 }}>
                                            {labDetails.lab.labName}
                                        </Typography>
                                        <Typography variant="body1" sx={{ color: "text.secondary", mb: 2 }}>
                                            {labDetails.lab.labDescription}
                                        </Typography>
                                        <Typography variant="body1" sx={{ fontWeight: "500", mb: 1 }}>
                                            Department: {labDetails.lab.department}
                                        </Typography>
                                        <Typography variant="body1" sx={{ color: "primary.main", mb: 1 }}>
                                            <Link href={labDetails.lab.website} target="_blank" rel="noopener noreferrer">
                                                {labDetails.lab.website}
                                            </Link>
                                        </Typography>
                                        <Typography variant="body1" sx={{ mb: 3 }}>
                                            Contact: <Link href={`mailto:${labDetails.lab.email}`}>{labDetails.lab.email}</Link>
                                        </Typography>

                                        <Button variant="contained" sx={{ mb: 3 }}>
                                            Express Interest
                                        </Button>
                                        <IconButton sx={{ ml: 2, borderRadius: "50%", bgcolor: "grey.200", "&:hover": { bgcolor: "grey.300" } }}>
                                            <StarBorderIcon />
                                        </IconButton>

                                        <Typography variant="h3" sx={{ fontSize: "1.5rem", fontWeight: "bold", mt: 4, mb: 2 }}>
                                            Open Opportunities
                                        </Typography>

                                        {labDetails.lab.positions.map((position, index) => (
                                            <Box
                                                key={index}
                                                sx={{
                                                    p: 3,
                                                    mb: 2,
                                                    borderRadius: 2,
                                                    boxShadow: 2,
                                                    bgcolor: "grey.100",
                                                    transition: "0.3s",
                                                    "&:hover": { boxShadow: 4, bgcolor: "grey.200" },
                                                }}
                                            >
                                                <Typography variant="h4" sx={{ fontSize: "1.25rem", fontWeight: "bold", mb: 1 }}>
                                                    {position.positionName}
                                                </Typography>
                                                <Typography variant="body1" sx={{ color: "text.secondary", mb: 1 }}>
                                                    {position.positionDescription}
                                                </Typography>
                                                <Typography variant="body2" sx={{ fontStyle: "italic", color: "text.disabled" }}>
                                                    Posted: {position.postedTimeStamp}
                                                </Typography>
                                            </Box>
                                        ))}
                                    </Box>
                                )}
                                {!loading && error && (
                                    <Typography variant="body1">Error fetching lab details</Typography>
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

export default GenericPage;