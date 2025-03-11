import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import { axiosPrivate } from '../../apis/backend';

const response = {
    errCode: '0',
    payload: {
        applications: {
            activeApplications: [
                {
                    labName: "Demo Lab 1",
                    positionName: "Demo Position 1",
                    submissionTimeStamp: "2024-04-19T06:56:20.544+00:00",
                    status: "Submitted"
                },
                {
                    labName: "Demo Lab 2",
                    positionName: "Demo Position 2",
                    submissionTimeStamp: "2024-04-20T06:56:20.544+00:00",
                    status: "Submitted"
                },
                {
                    labName: "Demo Lab 3",
                    positionName: "Demo Position 3",
                    submissionTimeStamp: "2024-04-21T06:56:20.544+00:00",
                    status: "Submitted"
                }
            ],
            archivedApplications: [
                {
                    labName: "Demo Lab 1",
                    positionName: "Demo Position 1",
                    submissionTimeStamp: "2024-04-19T06:56:20.544+00:00",
                    status: "Archived"
                },
                {
                    labName: "Demo Lab 2",
                    positionName: "Demo Position 2",
                    submissionTimeStamp: "2024-04-20T06:56:20.544+00:00",
                    status: "Archived"
                },
                {
                    labName: "Demo Lab 3",
                    positionName: "Demo Position 3",
                    submissionTimeStamp: "2024-04-21T06:56:20.544+00:00",
                    status: "Archived"
                }
            ]
        }
    }
};

const GenericPage = ({ title }) => {
    const userInfo = useSelector((state) => state.auth.userInfo);
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState(null);
    const [tabValue, setTabValue] = useState(0);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axiosPrivate.get('/application/studentList');
                setData(response.data.payload.applications);
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    return (
        <HelmetComponent title={"My Applications"}>
            <div className='GenericPage'>
                <Header />
                <main>
                    <Container maxWidth="lg">
                        <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                            <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                                <Typography variant="h1">My Applications</Typography>
                            </Box>
                        </Box>
                    </Container>
                    <Container maxWidth="lg">
                        <Paper className='GenericPage__container_paper' variant='outlined' sx={{ p: 2 }}>
                            <Tabs value={tabValue} onChange={handleTabChange} aria-label="application tabs">
                                <Tab label="Active" />
                                <Tab label="Archived" />
                            </Tabs>
                            {loading ? (
                                <Typography>Loading...</Typography>
                            ) : (
                                (tabValue === 0 ? data.activeApplications : data.archivedApplications).map((app, index) => (
                                    <Box key={index} sx={{ mb: 2, p: 2, border: '1px solid #ccc', borderRadius: '4px' }}>
                                        <Typography variant="h6">{app.labName}</Typography>
                                        <Typography variant="body1">{app.positionName}</Typography>
                                        <Typography variant="caption">Submitted: {new Date(app.submissionTimeStamp).toLocaleDateString()}</Typography>
                                        <Typography variant="body2" sx={{ mt: 1, fontWeight: 'bold', color: app.status === "Submitted" ? "green" : "gray" }}>
                                            {app.status}
                                        </Typography>
                                    </Box>
                                ))
                            )}
                        </Paper>
                    </Container>
                </main>
            </div>
        </HelmetComponent >
    );
}

export default GenericPage;
