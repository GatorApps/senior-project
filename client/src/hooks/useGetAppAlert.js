import { useDispatch } from 'react-redux';
import { setAppAlert } from '../context/alertSlice';
import { axiosPrivate } from '../apis/backend';

const useGetAppAlert = () => {
  const dispatch = useDispatch();

  const getAppAlert = async () => {
    try {
      const response = await axiosPrivate.get('/renderClient/appAlert');
      if (response?.data?.payload?.appAlert) {
        dispatch(setAppAlert(response?.data?.payload?.appAlert));
      } else {
        dispatch(setAppAlert({
          "maintenanceMode": true,
          "severity": "error",
          "title": "App Temporarily Unavailable",
          "message": "Oops! We're sorry, but we are unable to load this app at this time. Please check back soon",
          "actions": []
        }));
      }
    } catch (err) {
      if (err?.response?.data?.payload?.appAlert && err?.response?.data?.payload?.appAlert?.maintenanceMode === true) {
        dispatch(setAppAlert(err?.response?.data?.payload?.appAlert));
      } else {
        dispatch(setAppAlert({
          "maintenanceMode": true,
          "severity": "error",
          "title": "App Temporarily Unavailable",
          "message": "Oops! We're sorry, but we are unable to load this app at this time. Please check back soon",
          "actions": []
        }));
      }
    }
  };

  return getAppAlert;
};

export default useGetAppAlert;