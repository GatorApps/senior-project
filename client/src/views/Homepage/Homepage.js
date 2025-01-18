import { Fragment, useState } from 'react';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid2';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import GenericPageCard from '../../components/GenericPage/GenericPageCard';
import { useSelector } from 'react-redux';

const Homepage = () => {
  const userInfo = useSelector((state) => state.auth.userInfo);

  return (
    <HelmetComponent>
      <div className="Homepage">
        <Header />
        <main>
          {/* // General content displayed regardless of auth status  */}
          <div>

          </div>

          {/* // Logic to displace different content depending on if user is authed
        {userInfo?.roles.includes(100001) ? (
          // If authed
          <></>
        ) : (
          // If not
          <></>
        )} */}
          <Box component="workspace" sx={
            {
              flexGrow: 1,
              margin: '24px auto auto',
              padding: '0 16px',
              justifyContent: "space-between",
              'max-width': '1400px'
            }
          }>
            <Grid container spacing={2}>
              <Grid size={{ xs: 12, md: 6, lg: 4 }}>
                <GenericPageCard />
              </Grid>
              <Grid size={{ xs: 12, md: 6, lg: 4 }}>
                <GenericPageCard />
              </Grid>
              <Grid size={{ xs: 12, md: 6, lg: 4 }}>
                <GenericPageCard />
              </Grid>
              <Grid size={{ xs: 12, md: 6, lg: 4 }}>
                <GenericPageCard />
              </Grid>
              <Grid size={{ xs: 12, md: 6, lg: 4 }}>
                <Box minHeight={300}>
                  <GenericPageCard />
                </Box>
              </Grid>
              <Grid size={{ xs: 12, md: 6, lg: 4 }}>
                <GenericPageCard />
              </Grid>
              <Grid size={{ xs: 12, md: 6, lg: 4 }}>
                <GenericPageCard />
              </Grid>
              <Grid size={{ xs: 12, md: 6, lg: 4 }}>
                <GenericPageCard />
              </Grid>
            </Grid>
          </Box>

        </main>
      </div>
    </HelmetComponent>
  );
}

export default Homepage;
