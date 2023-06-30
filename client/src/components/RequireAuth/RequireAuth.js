import { Fragment, useState } from "react";
import { useLocation, Navigate, Outlet } from "react-router-dom";
import { useSelector } from 'react-redux';
import { Container } from '@mui/material';
import { axiosIdP } from '../../apis/backend';
import Header from '../Header/Header';
import Alert from '../Alert/Alert';
import ErrorPage from '../../views/ErrorPage/ErrorPage';

const RequireAuth = ({ allowedRoles }) => {
  const userInfo = useSelector((state) => state.auth.userInfo);
  const location = useLocation();

  const goToSignIn = async () => {
    if (!userInfo?.roles || userInfo.roles?.includes(100001)) return;

    try {
      const response = await axiosIdP.get(`/userAuth/getSignInUrl?continueTo=${window.location.href}`);
      if (response?.data?.payload) {
        return window.location.href = response?.data?.payload;
      } else {
        return window.location.href = "/signin";
      }
    } catch (error) {
      return window.location.href = "/signin";
    }
  };

  useState(goToSignIn, []);

  return (
    userInfo?.roles
      ? userInfo.roles?.find(role => allowedRoles?.includes(role))
        ? <Outlet />
        : userInfo.roles.includes(100001)
          ? <ErrorPage error='403' />
          : <Header loading />
      : <Fragment>
        <Header loading />
        <Container maxWidth="lg" sx={{ marginTop: '36px' }}>
          <Alert data={{
            severity: "error",
            title: "Unable to fetch user authentication status",
            message: "We are sorry, but we are unable to process your request at this time",
            actions: [
              { name: "Retry", onClick: () => { window.location.reload() } },
              { name: "Return Home", onClick: () => { window.location.href = "/" } }
            ]
          }} />
        </Container>
      </Fragment>
  );
};

export default RequireAuth;