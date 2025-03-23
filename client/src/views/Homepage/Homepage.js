import { Fragment, useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid2';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import GenericPageCard from '../../components/GenericPage/GenericPageCard';
import { useSelector } from 'react-redux';
import OpportunitySearchPageCard from '../../views/OpportunitySearchPage/OpportunitySearchPageCard';
import MyApplicationsPageCard from '../../views/MyApplicationsPage/MyApplicationsPageCard';
import MyPostingsPageCard from '../../views/MyPostingsPage/MyPostingsPageCard';
import ApplicationManagementPageCard from '../../views/ApplicationManagementPage/ApplicationManagementPageCard';
import NotificationsPageCard from '../../views/NotificationsPage/NotificationsPageCard';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';


const Homepage = () => {

  const userInfo = useSelector((state) => state.auth.userInfo);
  // userInfo?.roles?.includes(500301) for faculty/staff 
  // for student, its 500201

  const [view, setView] = useState(0);
  const [numColumns, setNumColumns] = useState(3);

  const handleChange = (event, newValue) => {
    setView(newValue);
  };

  const updateColumns = () => {
    if (window.innerWidth < 600) {
      setNumColumns(1);
    } else if (window.innerWidth < 960) {
      setNumColumns(2);
    } else {
      setNumColumns(3);
    }
  };

  useEffect(() => {
    updateColumns();
    window.addEventListener('resize', updateColumns);
    return () => window.removeEventListener('resize', updateColumns);
  }, []);

  const studentCards = [
    <OpportunitySearchPageCard />,
    <MyApplicationsPageCard />,
    <NotificationsPageCard />,
  ];

  const facultyCards = [
    <MyPostingsPageCard />,
    <ApplicationManagementPageCard />,
    <NotificationsPageCard />,
  ];

  const cards = view === 0 ? studentCards : facultyCards;
  const columns = Array.from({ length: numColumns }, () => []);

  cards.forEach((card, index) => {
    columns[index % numColumns].push(card);
  });

  return (
    <HelmetComponent>
      <div className="Homepage">
        <Header />
        <main>
          {/* // General content displayed regardless of auth status  */}
          <div>

          </div>

          {/* // Logic to displace different content depending on if user is authed
        {userInfo?.roles.includes(100001) ? (
          // If authed
          <></>
        ) : (
          // If not
          <></>
        )} */}
          <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
            <Tabs value={view} onChange={handleChange} aria-label="view selector">
              {userInfo?.roles?.includes(500201) && // Check if user is a student
                // Only show Student View tab if user is a student
                <Tab label="Student View" />
              }
              {userInfo?.roles?.includes(500301) && // Check if user is faculty/staff
                // Only show Faculty View tab if user is faculty/staff
                <Tab label="Faculty View" />
              }
            </Tabs>
          </Box>
          <Box
            component="workspace"
            sx={{
              display: 'flex',
              gap: '24px',
              justifyContent: 'center',
              margin: '36px auto 24px auto',
              padding: '0 16px',
              maxWidth: '1200px',
            }}
          >
            {columns.map((column, colIndex) => (
              <Box key={colIndex} sx={{ flex: 1, minWidth: '300px' }}>
                {column.map((card, cardIndex) => (
                  <Box key={cardIndex} sx={{ width: '350px', marginBottom: '24px' }}>
                    {card}
                  </Box>
                ))}
              </Box>
            ))}
          </Box>

        </main>
      </div>
    </HelmetComponent>
  );
}

export default Homepage;
