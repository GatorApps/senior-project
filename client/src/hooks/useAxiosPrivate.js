/* // https://www.youtube.com/watch?v=nI8PYZNFtac
import { axiosPrivate } from '../apis/backend';
import { useEffect } from 'react';
import { useSelector } from 'react-redux';
import useGetCSRFToken from './useGetCSRFToken';

const useAxiosPrivate = () => {
  const userInfo = useSelector((state) => state.auth.userInfo);
  const getCSRFToken = useGetCSRFToken();

  useEffect(() => {
    const requestIntercept = axiosPrivate.interceptors.request.use(
      config => {
        if (!config.headers['Authorization']) {
          config.headers['Authorization'] = `Bearer ${auth?.accessToken}`;
        }
        return config;
      }, (error) => Promise.reject(error)
    );

    const responseIntercept = axiosPrivate.interceptors.response.use(
      response => response,
      async (error) => {
        const prevRequest = error?.config;
        if (error?.response?.status === 403 && error?.response?.data?.invalidJWT && !prevRequest?.sent) {
          prevRequest.sent = true;
          const newAccessToken = await refresh();
          prevRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
          return axiosPrivate(prevRequest);
        }
        return Promise.reject(error);
      }
    );

    return () => {
      axiosPrivate.interceptors.request.eject(requestIntercept);
      axiosPrivate.interceptors.response.eject(responseIntercept);
    }
  }, [auth, refresh]);

  return axiosPrivate;
}

export default useAxiosPrivate; */