import { forwardRef, Fragment, useState, useEffect } from "react";
import { useSelector } from 'react-redux';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import { Box, Button, Container, Dialog, DialogActions, DialogTitle, DialogContent, DialogContentText, Divider, FormControl, Grid, Grow, InputAdornment, InputLabel, OutlinedInput, Paper, TextField, Tooltip, Typography } from '@mui/material';
import LoadingButton from '@mui/lab/LoadingButton';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import SaveIcon from '@mui/icons-material/Save';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { styled } from '@mui/material/styles';
import Alert from '../../components/Alert/Alert';
import GenericForm from '../../components/GenericForm/GenericForm';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import { axiosPrivate } from '../../apis/backend';
import useHandleData from '../../hooks/useHandleData';
import useGetUserInfo from '../../hooks/useGetUserInfo';

const Settings = () => {
  // User info from global context
  const refreshUserInfo = useGetUserInfo();

  // Fetch sections data
  //// Fetch profile section
  // const { response: userSettingsItemsResponse, loading: userSettingsItemsLoading, alert: userSettingsItemsAlert, retry: userSettingsItemsRetry } = useHandleData('get', '/appSettings/userSettings', { title: "user settings", retryButton: true });
  // Function for refreshing all sections
  // const refreshSections = () => {
  //   userSettingsItemsRetry();
  // }

  const applicantProfile = {
    "_id": "65ff111b93eaee3f7e220e38",
    "opid": "4d9c6082-c107-4828-95bf-d998953f8f80",
    "education": [
      {
        "institution": "University of Florida",
        "startDate": "2023-08-01T04:00:00.000Z",
        "degree": "Master of Science",
        "major": "Computer Science"
      },
      {
        "institution": "University of Florida",
        "startDate": "2019-08-01T04:00:00.000Z",
        "endDate": "2023-06-01T04:00:00.000Z",
        "degree": "Bachelor of Science",
        "major": "Computer Science",
        "description": "Mathematics Minor\nRelevant Coursework: Programing Fundamentals, Data Structures, Intro SWE, Linear Algebra\nGPA: 4.0"
      },
      {
        "institution": "Demo High School",
        "startDate": "2015-09-01T04:00:00.000Z",
        "endDate": "2019-06-01T04:00:00.000Z",
        "degree": "High School Diploma",
        "description": "Unweighted GPA: 4.0"
      }
    ],
    "experiences": [
      {
        "employer": "Demo Research Lab",
        "startDate": "2023-08-01T04:00:00.000Z",
        "title": "Graduate Student Researcher",
        "description": "Thanks to RESEARCH.UF for getting this position",
        "_id": "6601ca1a3ce1055e860f2116"
      },
      {
        "employer": "UF CISE Department",
        "startDate": "2022-08-01T04:00:00.000Z",
        "endDate": "2023-05-01T04:00:00.000Z",
        "title": "Teaching Assistant",
        "description": "• Test bulleted list sentence 1; this is just for testing\n• Test bulleted list sentence 2; this is another testing sentence\n• Last hurrah this is testing bulleted list sentence 3",
        "_id": "6601ca1a3ce1055e860f2117"
      },
      {
        "employer": "Demo Research Lab",
        "startDate": "2020-10-01T04:00:00.000Z",
        "endDate": "2023-06-01T04:00:00.000Z",
        "title": "Undergraduate Student Researcher",
        "description": "Found and applied to research position through RESEARCH.UF",
        "_id": "6601ca1a3ce1055e860f2118"
      },
      {
        "employer": "High School Experience",
        "startDate": "2017-09-01T04:00:00.000Z",
        "endDate": "2019-05-01T04:00:00.000Z",
        "title": "Testing Title",
        "description": "• Test bulleted list sentence 1; this is just for testing\n• Test bulleted list sentence 2; this is another testing sentence\n• Last hurrah this is testing bulleted list sentence 3",
        "_id": "6601ca1a3ce1055e860f2119"
      }
    ],
    "projects": [
      {
        "title": "RESEARCH.UF for Gator Apps",
        "startDate": "2023-01-01T05:00:00.000Z",
        "description": "• The Common App for campus research opportunities\n• Best platform for searching for and applying to research positions on campus",
        "_id": "6601ca1a3ce1055e860f211a"
      },
      {
        "title": "Dummy Project 2",
        "startDate": "2020-11-01T04:00:00.000Z",
        "endDate": "2022-06-01T04:00:00.000Z",
        "description": "• The Common App for campus research opportunities\n• Best platform for searching for and applying to research positions on campus",
        "_id": "6601ca1a3ce1055e860f211b"
      },
      {
        "title": "Dummy Project 3",
        "startDate": "2018-12-01T05:00:00.000Z",
        "endDate": "2021-05-01T04:00:00.000Z",
        "description": "• The Common App for campus research opportunities\n• Best platform for searching for and applying to research positions on campus",
        "_id": "6601ca1a3ce1055e860f211c"
      }
    ],
    "skills": [
      "JavaScript",
      "React",
      "Node.js",
      "MongoDB"
    ],
    "links": [
      {
        "title": "LinkedIn",
        "url": "https://www.linkedin.com/in/testing",
        "_id": "6601ca1a3ce1055e860f211d"
      },
      {
        "title": "GitHub",
        "url": "https://www.github.com/testing",
        "_id": "6601ca1a3ce1055e860f211e"
      }
    ],
    "additionalInformation": "• Test bulleted list sentence 1; this is just for additional information testing\n• Test bulleted list sentence 2; this is another testing sentence\n• Last hurrah this is testing bulleted list sentence 3",
    "__v": 0
  }

  const userInfo = useSelector((state) => state.auth.userInfo);

  const [educationData, setEducationData] = useState(applicantProfile.education[0]);
  const educationSchema = {
    "institution": {
      label: 'Institution Name',
      inputType: "string_singleline",
      required: true,
      mutable: true
    },
    "startDate": {
      label: 'Start Date',
      inputType: "date_month",
      required: true,
      mutable: true
    },
    "endDate": {
      label: 'End Date',
      inputType: "date_month",
      mutable: true
    },
    "degree": {
      label: 'Degree',
      inputType: "string_singleline",
      mutable: true
    },
    "major": {
      label: 'Major',
      inputType: "string_singleline",
      mutable: true
    },
    "description": {
      label: 'Description',
      inputType: "string_multiline",
      mutable: true
    }
  };

  return (
    <HelmetComponent title={"Settings"}>

      <div className='GenericPage SettingsPage'>
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
                <Box margin={3}>
                  <GenericForm key={'appSettings'} data={educationData} setData={setEducationData} schema={educationSchema} />
                </Box>
              </Paper>
            </Container>
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent>
  );
}

export default Settings;