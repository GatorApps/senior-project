import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';

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

  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      setData(response.payload.applications);
      setLoading(false);
    }, 2000);

    return () => clearTimeout(timer);
  }, []);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleButtonClick = () => {
    navigate('/myapplications');
  };

  return (
    <Box sx={{ width: '100%' }}>
      <Card variant='outlined'>
        <CardContent>
          <Typography gutterBottom variant="h2" component="div" sx={{ lineHeight: 1.2, fontSize: '1.3125rem', fontWeight: 400 }}>My Applications</Typography>
          <Paper elevation={0} sx={{ marginTop: '16px' }}>
            {loading ? (
              <SkeletonGroup boxPadding={'0'} />
            ) : (
              <>
                <Tabs value={tabValue} onChange={handleTabChange} aria-label="application tabs">
                  <Tab label="Active" />
                  <Tab label="Archived" />
                </Tabs>
                {(tabValue === 0 ? data.activeApplications : data.archivedApplications)
                  .slice(0, 2)
                  .map((app, index) => (
                    <div key={index} sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Typography variant="h7">{app.positionName}</Typography>
                      <Typography sx={{ marginX: '8px', fontWeight: '100', fontSize: '12px', color: app.status === "Submitted" ? "green" : "gray" }}>
                        {app.status}
                      </Typography>
                    </div>
                  ))}
              </>
            )}
          </Paper>
        </CardContent>

        <CardActions>
          <Button size="medium" onClick={handleButtonClick}>View All</Button>
        </CardActions>
      </Card>
    </Box>
  );
}

export default GenericPage;
