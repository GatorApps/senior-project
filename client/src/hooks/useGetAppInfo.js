import { useDispatch } from 'react-redux';
import { setAppInfo } from '../context/appSlice';
import { axiosPrivate } from '../apis/backend';

const useGetAppAlert = () => {
  const dispatch = useDispatch();

  const getAppAlert = async () => {
    try {
      const response = await axiosPrivate.get('/renderClient/appInfo');
      dispatch(setAppInfo(response?.data?.payload?.app));
    } catch (err) {
      if (err?.response?.data?.payload?.app?.alert && err?.response?.data?.payload?.app?.alert?.maintenanceMode === true) {
        dispatch(setAppInfo(err?.response?.data?.payload?.app));
      } else {
        dispatch(setAppInfo(
          {
            displayName: "GatorApps",
            alert: {
              "maintenanceMode": true,
              "severity": "error",
              "title": "App Temporarily Unavailable",
              "message": "Oops! We're sorry, but we are unable to load this app at this time. Please check back soon",
              "actions": []
            }
          }));
      }
    }
  };

  return getAppAlert;
};

export default useGetAppAlert;