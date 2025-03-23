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

const GenericPage = ({ title }) => {
    const userInfo = useSelector((state) => state.auth.userInfo);

    return (
        <HelmetComponent title={"Generic Page"}>
            <div className='GenericPage'>
                <Header />
                <main>
                    <Box>
                        <Container maxWidth="lg">
                            <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                                    <Typography variant="h1">Page Title</Typography>
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
                                <SkeletonGroup />
                                <SkeletonGroup />
                                <SkeletonGroup />
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