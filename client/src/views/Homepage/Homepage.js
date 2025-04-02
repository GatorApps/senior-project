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
import MessagesPageCard from '../Messages/MessagesCard';
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
    if (window.innerWidth < 825) {
      setNumColumns(1);
    } else if (window.innerWidth < 1200) {
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
    <MessagesPageCard />,
  ];

  const facultyCards = [
    <MyPostingsPageCard />,
    <ApplicationManagementPageCard />,
    <MessagesPageCard />,
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
          <Box
            sx={{
              width: '100%',
              backgroundColor: '#ffffff', // White background
              borderBottom: '1px solid #e0e0e0'
            }}
          >
            <Box
              sx={{
                maxWidth: '1200px',
                margin: '0 auto',
                padding: '0 16px'
              }}
            >
              <Tabs
                value={view}
                onChange={handleChange}
                aria-label="view selector"
                variant="standard"
                sx={{
                  '& .MuiTabs-indicator': {
                    backgroundColor: '#FA4616', // UF orange color for the indicator
                    height: '2px'
                  },
                  '& .MuiTabs-flexContainer': {
                    justifyContent: 'flex-start',
                  },
                  '& .MuiTab-root': {
                    color: '#333333', // Dark text for tabs
                    fontSize: '16px',
                    fontWeight: 400,
                    textTransform: 'none',
                    padding: '12px 16px',
                    minWidth: '120px',
                    '&.Mui-selected': {
                      color: 'rgb(40, 87, 151)', // UF blue color for active tab
                      fontWeight: 500
                    }
                  }
                }}
              >
                {userInfo?.roles?.includes(500201) && // Check if user is a student
                  // Only show Student View tab if user is a student
                  <Tab label="Student" />
                }
                {userInfo?.roles?.includes(500301) && // Check if user is faculty/staff
                  // Only show Faculty View tab if user is faculty/staff
                  <Tab label="Faculty / Staff" />
                }
              </Tabs>
            </Box>
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
                  <Box key={cardIndex} sx={{ width: '380px', marginBottom: '24px' }}>
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