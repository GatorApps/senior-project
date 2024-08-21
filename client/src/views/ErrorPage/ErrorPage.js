import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import { Container } from '@mui/material';
import Header from '../../components/Header/Header';
import Alert from '../../components/Alert/Alert';

const ErrorPage = ({ error }) => {
  const navigate = useNavigate();
  const [alertData, setAlertData] = useState();

  useEffect(() => {
    if (error === '403') {
      setAlertData({
        severity: "info",
        title: "Unauthorized",
        message: "We are sorry, but you do not have permission to view this resource.",
        actions: [{ name: "Home", onClick: () => { navigate('/'); } }]
      });
    } else if (error === '404') {
      setAlertData({
        severity: "info",
        title: "Not Found",
        message: "We are sorry, but the resource you requested does not exist.",
        actions: [{ name: "Home", onClick: () => { navigate('/'); } }]
      });
    };
  }, [error])

  return (
    <HelmetComponent title={alertData?.title}>
      <div className="ErrorPage">
        <Header />
        <Container maxWidth="lg" sx={{ marginTop: '36px' }}>
          <Alert data={alertData} />
        </Container>
      </div>
    </HelmetComponent>
  );
};

export default ErrorPage;