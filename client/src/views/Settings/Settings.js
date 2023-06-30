import { forwardRef, Fragment, useState, useEffect } from "react";
import { Box, Button, Container, Dialog, DialogActions, DialogTitle, DialogContent, DialogContentText, Divider, FormControl, Grid, Grow, InputAdornment, InputLabel, OutlinedInput, Paper, TextField, Tooltip, Typography } from '@mui/material';
import LoadingButton from '@mui/lab/LoadingButton';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import SaveIcon from '@mui/icons-material/Save';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { styled } from '@mui/material/styles';
import Alert from '../../components/Alert/Alert';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import Form from '../../components/Form/Form';
import { axiosPrivate } from '../../apis/backend';
import useHandleData from '../../hooks/useHandleData';
import useGetUserInfo from '../../hooks/useGetUserInfo';

const Settings = () => {
  // User info from global context
  const refreshUserInfo = useGetUserInfo();

  // Fetch sections data
  //// Fetch profile section
  const { response: userSettingsItemsResponse, loading: userSettingsItemsLoading, alert: userSettingsItemsAlert, retry: userSettingsItemsRetry } = useHandleData('get', '/appSettings/userSettings', { title: "user settings", retryButton: true });
  // Function for refreshing all sections
  const refreshSections = () => {
    userSettingsItemsRetry();
  }

  return (
    <div className='GenericPage'>
      <Header />
      <main>
        <Box>
          <Container maxWidth="lg">
            <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
              <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Typography variant="h1">Settings</Typography>
                <Button size="medium" sx={{ 'margin-left': '16px' }}>Button</Button>
              </Box>
              <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_right" sx={{ 'flex-grow': '1' }}>
                <Box className="GenericPage__container_title_flexBox_right">
                  <Button variant="contained" size="medium">Button</Button>
                </Box>
              </Box>
            </Box>
          </Container>
          <Container maxWidth="lg">
            <Paper className='GenericPage__container_paper' variant='outlined'>
              <Form loading={userSettingsItemsLoading} alert={userSettingsItemsAlert} items={userSettingsItemsResponse?.payload?.userSettingsItems} refresh={userSettingsItemsRetry} />
            </Paper>
          </Container>
        </Box>
      </main>
      <Footer />
    </div>
  );
}

export default Settings;