import * as React from 'react';
import { useEffect } from 'react';
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


const GenericPageCard = () => {
    const [loading, setLoading] = React.useState(true);
    const navigate = useNavigate();

    const handleButtonClick = () => {
        navigate('/search');
    };

    useEffect(() => {
        // Simulate loading
        const timer = setTimeout(() => {
            setLoading(false);
        }, 2000);

        // Cleanup the timer in case the component unmounts
        return () => clearTimeout(timer);
    }, []);

    return (
        <Box sx={{ width: '100%' }}>
            <Card variant="outlined">
                <CardContent>
                    <Typography gutterBottom variant="h2" component="div" sx={{ lineHeight: 1.2, fontSize: '1.3125rem', fontWeight: 400 }}>
                        Search for Opportunities
                    </Typography>
                    <Container maxWidth="lg">
                        <Box
                            className="GenericPage__container_title_box"
                            sx={{ marginBottom: '8px', display: 'flex' }}
                            component="div"
                            display="flex"
                            flexDirection="column"
                        >
                            {/* Search bar */}
                            <Box
                                sx={{ width: '100%', display: 'flex', alignItems: 'center', gap: '16px', justifyContent: 'flex-start' }}
                            >
                                <Autocomplete
                                    freeSolo
                                    sx={{ width: '100%', maxWidth: '100%', backgroundColor: 'white' }}
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
                                {/* <Button variant="contained" size="medium">Search</Button> */}
                            </Box>
                        </Box>
                    </Container>
                </CardContent>
                <CardActions>
                    <Button size="medium" onClick={handleButtonClick}>Take me to search page</Button>
                </CardActions>
            </Card>
        </Box>
    );
}

export default GenericPageCard;