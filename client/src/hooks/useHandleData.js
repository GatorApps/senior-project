import { useState, useEffect } from "react";
import { axiosPrivate } from '../apis/backend';

const useHandleData = (method, endpoint, { title, query = {}, body = {}, retryButton, alertActions = [] } = {}) => {
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [alert, setAlert] = useState(null);

  const handleData = async () => {
    // Rest states
    setLoading(true);
    setAlert(null);
    setResponse(null);
    // Add default retry button if requested
    const actions = [];
    // Deep copy so default retry function is not added to alertActions arg
    for (const action of alertActions) actions.push(action);
    if (retryButton) actions.push({ name: "Retry", onClick: () => { handleData() } });

    try {
      let response;
      if (method === 'get') {
        response = await axiosPrivate.get(endpoint, { params: query });
      } else if (method === 'post') {
        response = await axiosPrivate.post(endpoint, body, { params: query });
      } else if (method === 'put') {
        response = await axiosPrivate.put(endpoint, body, { params: query });
      } else if (method === 'delete') {
        response = await axiosPrivate.delete(endpoint, body, { params: query });
      }

      setResponse(response?.data);
    } catch (error) {
      setAlert({
        severity: error?.response?.data?.alertSeverity ? error.response.data.alertSeverity : "error",
        title: ("Oops! Unable to load " + ((title && title) || "content") + ": " + (error?.response?.data?.errCode || "Unknown error")),
        message: (error?.response?.data?.errMsg ? error?.response?.data?.errMsg : "We're sorry, but we are unable to load " + ((title && title) || "your requested content") + " at this time. Please try again later"),
        actions: actions
      });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    handleData();
  }, [endpoint]);

  return { response, loading, alert, retry: handleData };
}

export default useHandleData;