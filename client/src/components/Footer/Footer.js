import { Fragment, useState } from 'react';
import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';

const Footer = () => {
  return (
    <Fragment>
      <Divider sx={{ 'margin-top': '60px' }} />
      <Box component="footer" sx={
        {
          flexGrow: 1,
          margin: '24px auto auto',
          'max-width': '960px'
        }
      }>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={12} md={4}>
            <div>Footer Column 1</div>
          </Grid>
          <Grid item xs={12} sm={12} md={4}>
            <div>Column 2</div>
          </Grid>
          <Grid item xs={12} sm={12} md={4}>
            <div>Column 3</div>
          </Grid>
        </Grid>
      </Box>
    </Fragment>
  )
}

export default Footer;