import { Fragment, useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid2';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import GenericPageCard from '../../components/GenericPage/GenericPageCard';
import { useSelector } from 'react-redux';
import OpportunitySearchPageCard from '../../views/OpportunitySearchPage/OpportunitySearchPageCard';
import MyApplicationsPageCard from '../../views/MyApplicationsPage/MyApplicationsPageCard';

const Homepage = () => {
  const userInfo = useSelector((state) => state.auth.userInfo);

  const cards = [
    <OpportunitySearchPageCard />,
    <MyApplicationsPageCard />,
    <GenericPageCard />,
    <OpportunitySearchPageCard />,
    <MyApplicationsPageCard />,
    <GenericPageCard />
  ];

  // State to track the number of columns
  const [numColumns, setNumColumns] = useState(3);

  // Function to update number of columns based on screen size
  const updateColumns = () => {
    if (window.innerWidth < 600) {
      setNumColumns(1);
    } else if (window.innerWidth < 960) {
      setNumColumns(2);
    } else {
      setNumColumns(3);
    }
  };

  // Run once on mount & update on window resize
  useEffect(() => {
    updateColumns(); // Initial check
    window.addEventListener('resize', updateColumns);
    return () => window.removeEventListener('resize', updateColumns);
  }, []);

  // Distribute cards into columns while preserving row order
  const columns = Array.from({ length: numColumns }, () => []);

  cards.forEach((card, index) => {
    columns[index % numColumns].push(card); // Assign cards row-wise
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
                  <Box
                    key={cardIndex}
                    sx={{
                      width: '350px',
                      marginBottom: '24px',
                    }}
                  >
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
