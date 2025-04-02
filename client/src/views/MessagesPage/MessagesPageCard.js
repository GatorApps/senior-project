import * as React from 'react';
import { useEffect } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import { useNavigate } from 'react-router-dom';

const MessagesPageCard = () => {
  const [loading, setLoading] = React.useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    // Simulate loading
    const timer = setTimeout(() => {
      setLoading(false);
    }, 2000);

    // Cleanup the timer in case the component unmounts
    return () => clearTimeout(timer);
  }, []);

  return (
    <Box sx={{ width: '100%' }}>
      <Card variant="outlined">
        <CardContent>
          <Typography gutterBottom variant="h2" component="div" sx={{ lineHeight: 1.2, fontSize: '1.3125rem', fontWeight: 400 }}>
            Messages
          </Typography>
          <Box marginY="16px">
            {loading ?
              <SkeletonGroup boxPadding={'0'} /> :
              <Typography variant="body2" sx={{ fontSize: '0.938rem' }}>
                Notifications are coming soon! Stay tuned for updates and alerts related to your applications and postings.
              </Typography>
            }
          </Box>
        </CardContent>
        <CardActions>
          <Button size="medium" onClick={() => {
            navigate('/messages');
          }}>View All</Button>
        </CardActions>
      </Card>
    </Box>
  );
}

export default MessagesPageCard;