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
    const [applications, setApplications] = useState({ activeApplications: [], archivedApplications: [] });
    const [positions, setPositions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedTab, setSelectedTab] = useState(0);
    const [selectedPosition, setSelectedPosition] = useState('');
    const [selectedApplication, setSelectedApplication] = useState(null);

    const navigate = useNavigate();

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

                        {/* Application Tabs */}
                        <Tabs
                            sx={{ marginBottom: 0, height: 2 }}  // Decrease size of the tab 
                            value={selectedTab} onChange={(e, newValue) => setSelectedTab(newValue)} centered>
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
                                        <Box key={app.opid} sx={{ marginBottom: '2px', padding: '0px', borderBottom: '1px solid #ddd' }}>
                                            <Typography variant="h6">Student OPID: {app.opid}</Typography>
                                            <Typography variant="body2" color="textSecondary">
                                                Submitted: {new Date(app.submissionTimeStamp).toLocaleString()}
                                            </Typography>
                                        </Box>
                                    )) : selectedTab === 1 ?  // In Progress Applications
                                        applications.activeApplications.filter(app => app.status === 'inProgress').map((app) => (
                                            <Box key={app.opid} sx={{ marginBottom: '16px', padding: '16px', borderBottom: '1px solid #ddd' }}>
                                                <Typography variant="h6">Student OPID: {app.opid}</Typography>
                                                <Typography variant="body2" color="textSecondary">
                                                    Submitted: {new Date(app.submissionTimeStamp).toLocaleString()}
                                                </Typography>
                                            </Box>
                                        )) :  // Archived Applications
                                        applications.archivedApplications.map((app) => (
                                            <Box key={app.opid} sx={{ marginBottom: '0px', padding: '0px', borderBottom: '1px solid #ddd' }}>
                                                <Typography variant="h7">Student OPID: {app.opid}</Typography>
                                                <Typography variant="body2" color="textSecondary">
                                                    Submitted: {new Date(app.submissionTimeStamp).toLocaleString()}
                                                </Typography>
                                            </Box>
                                        ))
                            )}
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