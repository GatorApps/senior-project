import { Outlet } from 'react-router-dom';
import { Fragment, useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import Header from '../Header/Header';
import { Box, CircularProgress, Container } from '@mui/material';
import Alert from '../Alert/Alert';
import useGetUserInfo from '../../hooks/useGetUserInfo';
import useGetAppInfo from '../../hooks/useGetAppInfo';

const InitializeApp = () => {
  const getUserInfo = useGetUserInfo();
  const getAppInfo = useGetAppInfo();

  const [loading, setLoading] = useState(true);
  const appInfo = useSelector((state) => state.app.appInfo);

  //const { data: userAuthInfoData, loading: userAuthInfoLoading, alert: userAuthInfoAlert, reFetch: userAuthInfoReFetch } = useHandleData('');

  useEffect(() => {
    const initializeApp = async () => {
      // Get authenticated user info
      await getUserInfo();
      // Get app alert/maintenanceMode
      await getAppInfo();

      setLoading(false);
    }

    initializeApp();
  }, []);

  return (
    <div>
      {loading
        ? <Fragment>
          <Header loading />
          <Box align='center' sx={{
            'position': 'fixed',
            'top': '50%',
            'left': '50%',
            'transform': 'translate(-50%, -50%)'
          }}>
            <CircularProgress size="80px" sx={{ color: "rgb(224, 129, 46)" }} />
          </Box>
        </Fragment>
        : (appInfo.alert.maintenanceMode === true)
          ? <Fragment>
            <Header loading />
            <Container maxWidth="lg" sx={{ marginTop: '36px' }}>
              <Alert data={{
                severity: appInfo?.alert?.severity || "error",
                title: appInfo?.alert?.title || "Unnable to load app",
                message: appInfo?.alert?.message || "We are sorry, but we are unable to load this app at this time. Check back soon",
                actions: [
                  { name: "Retry", onClick: () => { window.location.reload() } },
                  { name: "Home", onClick: () => { window.location.href = "/" } }
                ]
              }} />
            </Container>
          </Fragment>
          : <Outlet />
      }
    </div>
  )
}

export default InitializeApp;