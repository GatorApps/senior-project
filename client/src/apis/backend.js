import axios from 'axios';

export default axios.create({
  baseURL: process.env.REACT_APP_SERVER_HOST + process.env.REACT_APP_SERVER_API_PATH,
  headers: { GATORAPPS_app: process.env.REACT_APP_APP_NAME },
});

export const axiosPrivate = axios.create({
  baseURL: process.env.REACT_APP_SERVER_HOST + process.env.REACT_APP_SERVER_API_PATH,
  headers: { GATORAPPS_app: process.env.REACT_APP_APP_NAME, 'Content-Type': 'application/json' },
  withCredentials: true
});

export const axiosIdP = axios.create({
  baseURL: process.env.REACT_APP_IDP_HOST + process.env.REACT_APP_IDP_API_PATH,
  headers: { GATORAPPS_app: process.env.REACT_APP_APP_NAME, 'Content-Type': 'application/json' },
  withCredentials: true
});