import Box from '@mui/material/Box';
import Skeleton from '@mui/material/Skeleton';

const SkeletonGroup = (props) => {
  return(
    <Box sx={{ padding: props.boxPadding||'16px' }}>
      <Skeleton />
      <Skeleton width="60%" />
      <Skeleton width="80%" />
    </Box>
  )
}

export default SkeletonGroup;