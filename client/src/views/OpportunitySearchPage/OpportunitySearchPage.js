import { useDispatch, useSelector } from 'react-redux';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import React from 'react';

import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import SearchIcon from '@mui/icons-material/Search';

const response = {
  errCode: '0',
  payload: {
    positions: [
      {
        labName: "Demo Lab 1",
        positionName: "Demo Position 1",
        positionDescription: "Demo Position Description 1"
      },
      {
        labName: "Demo Lab 2",
        positionName: "Demo Position 1",
        positionDescription: "Demo Position Description 2"
      },
      {
        labName: "Demo Lab 2",
        positionName: "Demo Position 2",
        positionDescription: "Demo Position Description 3"
      }
    ]
  }
}

const response2 = {
  errCode: '0',
  payload: {
    positions: []
  }
}

const GenericPage = ({ title }) => {
  const userInfo = useSelector((state) => state.auth.userInfo);

  // Set loading state
  const [loading, setLoading] = React.useState(true);
  const [positions, setPositions] = React.useState([]);
  const [error, setError] = React.useState(null);
  const dispatch = useDispatch();
  React.useEffect(() => {
    // Simulate loading
    const timer = setTimeout(() => {
      setLoading(false);
      setPositions(response.payload.positions);
    }, 2000);

    // Cleanup the timer in case the component unmounts
    return () => clearTimeout(timer);
  }, []);

  return (
    <HelmetComponent title={"Search for Opportunities"}>
      <div className='GenericPage'>
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">Search for Opportunities</Typography>
                </Box>
              </Box>
            </Container>
            <Container maxWidth="lg">
              <Paper className='GenericPage__container_paper' variant='outlined'>
                {/* Search bar */}
                <Box margin="30px" sx={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <Autocomplete
                    freeSolo
                    sx={{ width: 500, maxWidth: '100%', backgroundColor: 'white' }}
                    options={response.payload.positions.map((option) => option.positionName)}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        variant="outlined"
                        size="small"
                        placeholder="Search..."
                        InputProps={{
                          ...params.InputProps,
                          startAdornment: (
                            <InputAdornment position="start">
                              <SearchIcon />
                            </InputAdornment>
                          ),
                        }}
                      />
                    )}
                  />
                  <Button variant="contained" size="medium">Search</Button>
                </Box>

                {/* Search results */}
                <Box margin="30px">
                  {loading ?
                    <>
                      <SkeletonGroup />
                      <SkeletonGroup />
                      <SkeletonGroup />
                    </>
                    :
                    positions.map((position, index) => (
                      <Box
                        key={index}
                        marginBottom="16px"
                        display="flex"
                        flexDirection="column"
                        alignItems="flex-start"
                        justifyContent="center"
                        sx={{ padding: '16px', borderBottom: '1px solid #e0e0e0' }}
                        className="GenericPage__position_card"
                      >
                        <Container maxWidth="lg">
                          <Typography variant="h7">{position.labName}</Typography>
                          <Typography variant="h5">{position.positionName}</Typography>
                          <Typography variant="body2">{position.positionDescription}</Typography>
                        </Container>
                      </Box>
                    ))
                  }
                </Box>

                {/* If there is an error, display it */}
                {error &&
                  <Typography
                    variant="body2"
                    color="error"
                  >
                    {error}
                  </Typography>
                }

                {/* If there are no positions, display a message */}
                {!loading && positions.length === 0 &&
                  <Typography
                    variant="body2"
                    sx={{ marginTop: '16px', textAlign: 'center' }}
                    color="textSecondary"
                    component="div"
                  >
                    No positions found.
                  </Typography>
                }

              </Paper>
            </Container>
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent >
  );
}

export default GenericPage;