import { useDispatch } from 'react-redux';
import { setUserInfo } from '../context/authSlice';
import { axiosIdP } from '../apis/backend';

const useGetUserInfo = () => {
  const dispatch = useDispatch();

  const getUserInfo = async () => {
    await axiosIdP.get('/userAuth/getUserAuthInfo')
      .then(response => {
        dispatch(setUserInfo(response?.data?.payload));
      })
      .catch(error => {
        // TO DO: Only give roles if explicitly knows that user has not signed in
        if (error?.response?.data?.errCode) dispatch(setUserInfo({ roles: [] }));
      })
  };

  return getUserInfo;
};

export default useGetUserInfo;