import { useDispatch, useSelector } from 'react-redux';
import { axiosPrivate } from '../../apis/backend';
import { Link, useLocation } from 'react-router-dom';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import Footer from '../../components/Footer/Footer';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import React, { useState, useEffect, useCallback } from 'react';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Select from '@mui/material/Select';
import SearchIcon from '@mui/icons-material/Search';
import Pagination from '@mui/material/Pagination';
import MenuItem from '@mui/material/MenuItem';
import InputLabel from '@mui/material/InputLabel';
import FormControl from '@mui/material/FormControl';
import Toolbar from '@mui/material/Toolbar';
import { debounce } from 'lodash';
import { useNavigate } from 'react-router-dom';
import { South } from '@mui/icons-material';


const GenericPage = ({ title }) => {
  const userInfo = useSelector((state) => state.auth.userInfo);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [size, setSize] = useState(5);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalCount, setTotalCount] = useState(1);

  // Get search param from URL
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);

  // Get search query from URL and decode it
  const searchQuery = searchParams.get('q');

  const [positions, setPositions] = useState([]);
  const [searchOptions, setSearchOptions] = useState([]);
  const [searchText, setSearchText] = useState('');

  // Fetch initial positions
  useEffect(() => {
    setLoading(true);
    if (searchQuery) {
      setSearchText(searchQuery);
      searchPositions(searchQuery, page, size);
    } else {
      setLoading(false);
    }
  }, [searchQuery]);

  const searchPositions = (text, currPage, currSize) => {
    console.log("size: " + currSize + "  page : " + currPage + "  totalPages: " + totalPages);
    setLoading(true);
    axiosPrivate.get(`/posting/searchList?q=${text}&size=${currSize}&page=${currPage}`)
      .then((response) => {
        setPositions(response.data.payload.positions || []);
        setTotalPages(response.data.payload.totalPages || 1);
        setTotalCount(response.data.payload.totalCount || 1);
        setLoading(false);
      })
      .catch((error) => {
        setError(error);
        setLoading(false);
      });
  };


  // handle page change
  const handlePageChange = (newPage) => {
    console.log("change from " + page + " to " + newPage);
    setPage(newPage);
    searchPositions(searchQuery, newPage, size);
  }

  // handle changing page size
  const handlePageSizeChange = (event) => {
    console.log("new page size: " + event.target.value);
    setSize(event.target.value);
    setPage(0);
    searchPositions(searchText, 0, event.target.value);
  }

  // handles new search
  const handleSearchButton = () => {
    setPage(0);
    searchPositions(searchText, 0, size);
  }


  // Debounced API call for search
  const fetchSearchOptions = useCallback(
    debounce((query) => {
      if (query.trim() === '') {
        setSearchOptions([]);
        return;
      }
      axiosPrivate.get(`/posting/searchIndexer?q=${query}`)
        .then((response) => {
          setSearchOptions(response.data.payload.positions || []);
        })
        .catch(() => {
          setSearchOptions([]);
        });
    }, 500),
    []
  );

  // Handle input change and trigger API search
  const handleSearchChange = (event, value) => {
    setSearchText(value);
    fetchSearchOptions(value);
  };

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
                    sx={{ width: '100%', maxWidth: '100%', backgroundColor: 'white' }}
                    value={searchText}
                    onChange={(event, newValue) => {
                      if (typeof newValue === 'string') {
                        setSearchText(newValue); // Handle text input
                        searchPositions(newValue);
                      } else if (newValue && newValue.positionName) {
                        setSearchText(newValue.positionName); // Handle new option
                        searchPositions(newValue.positionName);
                      } else {
                        setSearchText(''); // Reset if invalid
                      }
                    }}
                    onInputChange={handleSearchChange}
                    options={searchOptions}
                    getOptionLabel={(option) => (typeof option === "string" ? option : option.positionName || "")}
                    renderInput={(params) => (
                      <TextField
                        {...params}
                        variant="outlined"
                        size="small"
                        placeholder="Search..."
                        sx={{
                          ...params.sx,
                          height: '65px', // Set the height of the TextField here
                          '& .MuiInputBase-root': {
                            height: '100%', // Ensure the input field inside has the correct height
                          },
                        }}
                        InputProps={{
                          ...params.InputProps,
                          startAdornment: (
                            <InputAdornment position="start">
                              <SearchIcon />
                            </InputAdornment>
                          ),
                        }}
                        onKeyDown={(event) => {
                          if (event.key === 'Enter') {
                            if (searchText.trim() !== '') {
                              handleSearchButton();
                            }
                          }
                        }}
                      />
                    )}
                  />
                  <Button variant="contained" size="medium" onClick={() => handleSearchButton()}>Search</Button>
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
                          <Link to={`/posting?postingId=${position.positionId}`} target="_blank">
                            <Typography variant="h5" marginBottom="24px">{position.positionName}</Typography>
                          </Link>
                          <Box dangerouslySetInnerHTML={{ __html: position.positionDescription }} />
                        </Container>
                      </Box>
                    ))
                  }
                </Box>

                {/* Error message */}
                {error && (
                  <Typography variant="body2" color="error">
                    {error.message || "An error occurred while fetching data."}
                  </Typography>
                )}

                {/* No positions found message */}
                {!loading && positions.length === 0 && (
                  <Typography
                    variant="body2"
                    sx={{ marginTop: '16px', textAlign: 'center' }}
                    color="textSecondary"
                    component="div"
                  >
                    No positions found.
                  </Typography>
                )}


                {/* Page Size and Pagination Controls */}
                <Box margin="20px 0" sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px' }}>

                  {/* Page Size Selector */}
                  <Box sx={{ width: '100%', maxWidth: 250 }}>
                    <FormControl fullWidth>
                      <InputLabel>Page Size</InputLabel>
                      <Select
                        value={size}
                        onChange={handlePageSizeChange}
                        label="Page Size"
                      >
                        <MenuItem value={5}>5</MenuItem>
                        <MenuItem value={10}>10</MenuItem>
                        <MenuItem value={15}>15</MenuItem>
                        <MenuItem value={30}>30</MenuItem>
                      </Select>
                    </FormControl>
                  </Box>

                  {/* Display Total Results */}
                  <Box>
                    <Typography variant="body2" color="textSecondary">
                      {`Displaying ${Math.min(page * size + 1, totalCount)}-${Math.min((page + 1) * size, totalCount)} of ${totalCount}`}
                    </Typography>
                  </Box>

                  {/* Pagination Controls */}
                  <Box>
                    <Pagination
                      count={totalPages}
                      page={page + 1}  // Adjust to 1-based pagination
                      onChange={(event, newPage) => handlePageChange(newPage - 1)}  // Adjust for 0-based page index
                      color="primary"
                    />
                  </Box>

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

export default GenericPage;
