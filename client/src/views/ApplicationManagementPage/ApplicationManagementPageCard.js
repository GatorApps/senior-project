import * as React from 'react';
import { useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup'; // Assuming you have a SkeletonGroup component for loading state
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import { axiosPrivate } from '../../apis/backend'
import { useNavigate } from 'react-router-dom';


const GenericPageCard = () => {
    const [applications, setApplications] = useState({ activeApplications: [], movingApplications: [], archivedApplications: [] });
    const [positions, setPositions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedTab, setSelectedTab] = useState(0);
    const [selectedPosition, setSelectedPosition] = useState('');
    const [selectedApplication, setSelectedApplication] = useState(null);
    const navigate = useNavigate();

    const updateApplicationStatus = async (app, newStatus) => {
        try {
            await axiosPrivate.put(`/application/applicationStatus`, null, {
                params: {
                    labId: app.labId,
                    applicationId: app.applicationId,
                    status: newStatus
                }
            });
            fetchApplications();
        } catch (error) {
            setError("Error updating status");
        }
    };

    const fetchApplications = async () => {
        if (selectedPosition) {
            setLoading(true);
            axiosPrivate.get(`/application/applicationManagement?positionId=${selectedPosition}`)
                .then((response) => {
                    if (response.data && response.data.errCode === "0" && response.data.payload.applications) {
                        setApplications(response.data.payload.applications);
                        // console.log(response.data.payload.applications);
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
    };

    // Fetch positions from API
    useEffect(() => {
        axiosPrivate.get('/posting/postingsList')
            .then((response) => {
                // console.log(response.data);
                if (response.data && response.data.errCode === "0" && response.data.payload.positions) {
                    setPositions(response.data.payload.positions);
                    setSelectedPosition(response.data.payload.positions[0].positionId);
                } else {
                    setError("No positions found");
                }
            })
            .catch((error) => {
                setError(error.message || "Error fetching positions");
            });
        setLoading(false);
    }, []);

    // Fetch applications when a position is selected
    useEffect(() => {
        fetchApplications();
    }, [selectedPosition]);

    const handleViewApplication = (application) => {
        console.log(application);
        setSelectedApplication(application);
    }

    return (
        <Box sx={{ width: '100%' }}>
            <Card variant="outlined">
                <CardContent>
                    <Typography gutterBottom variant="h2" component="div" sx={{ lineHeight: 1.2, fontSize: '1.3125rem', fontWeight: 400 }}>
                        Application Management
                    </Typography>
                    <Box marginY="16px">
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, marginBottom: 2 }}>
                            {/* Decrease size of the select box */}
                            <FormControl sx={{ minWidth: 200, backgroundColor: 'white' }}>
                                <InputLabel id="position-select-label">Position</InputLabel>
                                <Select
                                    labelId="position-select-label"
                                    id="position-select"
                                    value={selectedPosition}
                                    onChange={(e) => setSelectedPosition(e.target.value)}
                                    sx={{ minWidth: 200, backgroundColor: 'white', height: '40px' }}  // Decrease size of the select box
                                    label="Position"
                                >
                                    {positions.map((pos) => (
                                        <MenuItem key={pos.positionId} value={pos.positionId}>
                                            {pos.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Box>

                        {/* Application Tabs (Shrink the size) */}
                        <Tabs
                            value={selectedTab}
                            onChange={(e, newValue) => setSelectedTab(newValue)}
                            centered
                        >
                            <Tab label="Active" sx={{ fontSize: '0.75rem' }} />
                            <Tab label="Moving Forward" sx={{ fontSize: '0.75rem' }} />
                            <Tab label="Archived" sx={{ fontSize: '0.75rem' }} />
                        </Tabs>

                        {/* Application List */}
                        <Paper variant="outlined" sx={{ padding: '6px', marginTop: 2 }}>
                            {loading ? (
                                <SkeletonGroup boxPadding={'0'} />
                            ) : error ? (
                                <Typography color="error">{error}</Typography>
                            ) : ([applications.activeApplications, applications.movingApplications, applications.archivedApplications][selectedTab].length === 0 ? (
                                <Typography variant="body1" color="textSecondary" sx={{ marginBottom: '8px', padding: '16px' }}>No applications available.</Typography>
                            ) : (
                                [applications.activeApplications, applications.movingApplications, applications.archivedApplications][selectedTab].slice(0, 3).map((app) => (
                                    <Box key={app.opid} sx={{ marginBottom: '16px', padding: '16px', borderBottom: '1px solid #ddd' }}>
                                        <Typography variant="h7">Applicant Name: {app.firstName} {app.lastName}</Typography>
                                        {/* <Typography variant="h6">Student OPID: {app.opid}</Typography> */}
                                        <Typography variant="body2" color="textSecondary">
                                            Submitted: {new Date(app.submissionTimeStamp).toLocaleString()}
                                        </Typography>
                                    </Box>
                                ))
                            ))}
                        </Paper>
                    </Box>
                </CardContent>
                <CardActions>
                    <Button size="medium" onClick={() => navigate('/applicationManagement')}>View All</Button>
                </CardActions>
            </Card>
        </Box>
    );
}

export default GenericPageCard;