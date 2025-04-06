import React, { useEffect, useState } from 'react';
import { axiosPrivate } from '../../apis/backend';
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
import Link from '@mui/material/Link';
import { Divider } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const MyApplicationsCard = ({ title }) => {
  const userInfo = useSelector((state) => state.auth.userInfo);
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState(null);
  const [tabValue, setTabValue] = useState(0);

  const navigate = useNavigate();

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

  const handleButtonClick = () => {
    navigate('/myapplications');
  };

  return (
    <Box sx={{ width: '100%' }}>
      <Card variant='outlined'>
        <CardContent sx={{ height: '250px', display: 'flex', flexDirection: 'column', marginBottom: '8px' }}>
          <Typography gutterBottom variant="h2" component="div" sx={{ lineHeight: 1.2, fontSize: '1.3125rem', fontWeight: 400 }}>My Applications</Typography>
          <Paper elevation={0} sx={{ marginTop: '16px' }}>
            {loading ? (
              <SkeletonGroup boxPadding={'0'} />
            ) : (
              <>
                <Tabs value={tabValue} onChange={handleTabChange} aria-label="application tabs" sx={{ marginBottom: '16px' }}>
                  <Tab label="Active" />
                  <Tab label="Archived" />
                </Tabs>
                {(tabValue === 0 ? data?.activeApplications : data?.archivedApplications)
                  ?.slice(0, 2)
                  ?.map((app, index) => (
                    <Link href={`/posting?postingId=${app.positionId}`} target="_blank" style={{ textDecoration: 'none', color: "black" }}>
                      <Box key={app.applicationId} sx={{
                        px: 1.5,
                        py: 1,
                        cursor: 'pointer',
                        borderRadius: 1,
                        mb: 0.5,
                        '&:hover': {
                          bgcolor: 'action.hover',
                        }
                      }}>
                        <Typography variant="h7">{app.positionName}</Typography>
                        <Typography sx={{ marginX: '8px', fontWeight: '100', fontSize: '12px', color: app.status === "submitted" ? "green" : "gray" }}>
                          {app.status}
                        </Typography>
                      </Box>
                      <Divider sx={{ my: 0.5 }} />
                    </Link>
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
};

export default MyApplicationsCard;
