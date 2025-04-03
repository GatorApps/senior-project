import * as React from 'react';
import { useEffect, useState, useCallback } from 'react';
import { axiosPrivate } from '../../apis/backend';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import SkeletonGroup from '../../components/SkeletonGroup/SkeletonGroup';
import { useNavigate } from 'react-router-dom';

import Container from '@mui/material/Container';

import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import SearchIcon from '@mui/icons-material/Search';

import { debounce } from 'lodash';


const OpportunitySearchCard = () => {
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState(null);
  const navigate = useNavigate();

  const [positions, setPositions] = useState([]);
  const [searchOptions, setSearchOptions] = useState([]);
  const [searchText, setSearchText] = useState('');

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

  const navigateSearch = (text) => {
    if (text.trim() === '') return;
    navigate(`/search?q=${text}`);
  };

  return (
    <Box sx={{ width: '100%' }}>
      <Card variant="outlined">
        <CardContent sx={{ height: '175px' }}>
          <Typography gutterBottom variant="h2" component="div" sx={{ lineHeight: 1.2, fontSize: '1.3125rem', fontWeight: 400 }}>
            Search for Opportunities
          </Typography>
          <div maxWidth="lg">
            <Box
              className="GenericPage__container_title_box"
              sx={{ marginTop: '32px' }}
              component="div"
              display="flex"
              flexDirection="column"
              justifyContent="space-between"
              alignItems="center"
            >
              {/* Search bar */}
              <Box
                sx={{ width: '100%', display: 'flex', alignItems: 'center', gap: '16px', justifyContent: 'flex-start' }}
              >
                <Autocomplete
                  freeSolo
                  disableClearable
                  sx={{ width: 500, maxWidth: '100%', backgroundColor: 'white' }}
                  value={searchText}
                  onChange={(event, newValue) => {
                    if (typeof newValue === 'string') {
                      setSearchText(newValue); // Handle text input
                      navigateSearch(newValue);
                    } else if (newValue && newValue.positionName) {
                      setSearchText(newValue.positionName); // Handle selection
                      navigateSearch(newValue.positionName);
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
                          navigate(`/search?q=${searchText}`);
                        }
                      }}
                    />
                  )}
                />
                {/* <Button variant="contained" size="medium">Search</Button> */}
              </Box>
            </Box>
          </div>
        </CardContent>
        {/* <CardActions>
                    <Button size="medium" onClick={handleButtonClick}>Take me to search page</Button>
                </CardActions> */}
      </Card>
    </Box>
  );
}

export default OpportunitySearchCard
