import * as React from 'react';
import { useEffect } from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import SkeletonGroup from '../SkeletonGroup/SkeletonGroup';

const GenericPageCard = () => {
  const [loading, setLoading] = React.useState(true);

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
            Generic Workspace Card
          </Typography>
          <Box marginY="16px">
            {loading ?
              <SkeletonGroup boxPadding={'0'} /> :
              <Typography variant="body2" sx={{ fontSize: '0.938rem' }}>
                This is a generic paragraph of texts. This is a generic paragraph of texts. This is a generic paragraph of texts. This is a generic paragraph of texts.
              </Typography>
            }
          </Box>
        </CardContent>
        <CardActions>
          <Button size="medium">Action One</Button>
          <Button size="medium">Action Two</Button>
        </CardActions>
      </Card>
    </Box>
  );
}

export default GenericPageCard;