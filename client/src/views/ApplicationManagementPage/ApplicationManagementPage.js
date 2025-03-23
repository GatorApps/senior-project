import { useEffect, useState } from 'react';
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
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import { FormControl, InputLabel } from '@mui/material';
import ApplicationViewer from '../../components/ApplicationViewerPopup/ApplicationViewerPopup';

const ApplicationManagement = () => {
    const [applications, setApplications] = useState({ activeApplications: [], archivedApplications: [] });
    const [positions, setPositions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedTab, setSelectedTab] = useState(0);
    const [selectedPosition, setSelectedPosition] = useState('');
    const [selectedApplication, setSelectedApplication] = useState(null);

    // Fetch positions from API
    useEffect(() => {
        // axiosPrivate.get('/posting/postingsList')
        //     .then((response) => {
        //         console.log(response.data);
        //         if (response.data && response.data.errCode === "0" && response.data.payload.positions) {
        //             setPositions(response.data.payload.positions);
        //         } else {
        //             setError("No positions found");
        //         }
        //     })
        //     .catch((error) => {
        //         setError(error.message || "Error fetching positions");
        //     });
        // Mock data for positions
        const mockPositions = [
            {
                "positionId": "67ddeb5144c53033691a78ff",
                "name": "Eric's Testing Position"
            },
            {
                "positionId": "67ddf20744c53033691a7906",
                "name": "Eric's Testing Position 2"
            },
            {
                "positionId": "67ddf22f44c53033691a7907",
                "name": "Eric's Testing Position 3"
            },
            {
                "positionId": "67ddf24344c53033691a7908",
                "name": "Eric's Testing Position 4"
            }
        ]
        setPositions(mockPositions);
        setSelectedPosition(mockPositions[0].positionId);
        setLoading(false);
    }, []);

    // Fetch applications when a position is selected
    useEffect(() => {
        if (selectedPosition) {
            setLoading(true);
            axiosPrivate.get(`/application/applicationManagement?positionId=${selectedPosition}`)
                .then((response) => {
                    if (response.data && response.data.errCode === "0" && response.data.payload.applications) {
                        setApplications(response.data.payload.applications);
                    } else {
                        setError("No applications found");
                    }
                    setLoading(false);
                })
                .catch((error) => {
                    setError(error.message || "Error fetching applications");
                    setLoading(false);
                });
        }
    }, [selectedPosition]);

    return (
        <HelmetComponent title={"Application Management"}>
            <div className='ApplicationManagement'>
                <Header />
                <main>
                    <Box sx={{ padding: '16px', backgroundColor: '#f5f5f5' }}>
                        <Container maxWidth="lg">
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                                <Box>
                                    <Typography variant="h5" component="h2">
                                        Application Management
                                    </Typography>
                                    <Typography variant="subtitle1" color="textSecondary">
                                        Manage applications for your positions here.
                                    </Typography>
                                </Box>
                            </Box>

                            {/* Position Dropdown */}
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, marginBottom: 2 }}>
                                <FormControl sx={{ minWidth: 200, backgroundColor: 'white' }}>
                                    <InputLabel id="position-select-label">Select Position</InputLabel>
                                    <Select
                                        labelId="position-select-label"
                                        id="position-select"
                                        value={selectedPosition}
                                        onChange={(e) => setSelectedPosition(e.target.value)}
                                        sx={{ minWidth: 200 }}
                                        label="Select Position"
                                    >
                                        {positions.map((pos) => (
                                            <MenuItem key={pos.positionId} value={pos.positionId}>
                                                {pos.name}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Box>

                            {/* Application Tabs */}
                            <Tabs value={selectedTab} onChange={(e, newValue) => setSelectedTab(newValue)} centered>
                                <Tab label="New" />
                                <Tab label="In Progress" />
                                <Tab label="Archived" />
                            </Tabs>

                            {/* Application List */}
                            <Paper variant="outlined" sx={{ padding: '24px', marginTop: 2 }}>
                                {loading ? (
                                    <SkeletonGroup boxPadding={'0'} />
                                ) : error ? (
                                    <Typography color="error">{error}</Typography>
                                ) : applications.activeApplications.length === 0 && applications.archivedApplications.length === 0 ? (
                                    <Typography variant="body1" color="textSecondary">No applications available.</Typography>
                                ) : (
                                    selectedTab === 0 ?  // New Applications
                                        applications.activeApplications.map((app) => (
                                            <Box key={app.opid} sx={{ marginBottom: '16px', padding: '16px', borderBottom: '1px solid #ddd' }}>
                                                <Typography variant="h6">Student OPID: {app.opid}</Typography>
                                                <Typography variant="body2" color="textSecondary">
                                                    Submitted: {new Date(app.submissionTimeStamp).toLocaleString()}
                                                </Typography>
                                                <Box sx={{ display: 'flex', gap: 2, marginTop: 1 }}>
                                                    <Button variant="contained" onClick={() => setSelectedApplication(app)}>View</Button>
                                                    <Button variant="outlined">{app.status.charAt(0).toUpperCase() + app.status.slice(1)}</Button>
                                                </Box>
                                            </Box>
                                        )) : selectedTab === 1 ?  // In Progress Applications
                                            applications.activeApplications.filter(app => app.status === 'inProgress').map((app) => (
                                                <Box key={app.opid} sx={{ marginBottom: '16px', padding: '16px', borderBottom: '1px solid #ddd' }}>
                                                    <Typography variant="h6">Student OPID: {app.opid}</Typography>
                                                    <Typography variant="body2" color="textSecondary">
                                                        Submitted: {new Date(app.submissionTimeStamp).toLocaleString()}
                                                    </Typography>
                                                    <Box sx={{ display: 'flex', gap: 2, marginTop: 1 }}>
                                                        <Button variant="contained" onClick={() => setSelectedApplication(app)}>View</Button>
                                                        <Button variant="outlined">{app.status.charAt(0).toUpperCase() + app.status.slice(1)}</Button>
                                                    </Box>
                                                </Box>
                                            )) :  // Archived Applications
                                            applications.archivedApplications.map((app) => (
                                                <Box key={app.opid} sx={{ marginBottom: '16px', padding: '16px', borderBottom: '1px solid #ddd' }}>
                                                    <Typography variant="h6">Student OPID: {app.opid}</Typography>
                                                    <Typography variant="body2" color="textSecondary">
                                                        Submitted: {new Date(app.submissionTimeStamp).toLocaleString()}
                                                    </Typography>
                                                    <Box sx={{ display: 'flex', gap: 2, marginTop: 1 }}>
                                                        <Button variant="contained" onClick={() => setSelectedApplication(app)}>View</Button>
                                                        <Button variant="outlined">{app.status.charAt(0).toUpperCase() + app.status.slice(1)}</Button>
                                                    </Box>
                                                </Box>
                                            ))
                                )}
                            </Paper>
                        </Container>

                        <ApplicationViewer
                            open={Boolean(selectedApplication)}
                            onClose={() => setSelectedApplication(null)}  // Close the viewer when the close button is clicked
                            application={selectedApplication}  // Pass the selected application to the viewer
                        />
                    </Box>
                </main>
                <Footer />
            </div>
        </HelmetComponent>
    );
}

export default ApplicationManagement;
