import { Fragment, useState } from 'react';
import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header';
import { useSelector } from 'react-redux';

const Homepage = () => {
  const userInfo = useSelector((state) => state.auth.userInfo);

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
        </main>
      </div>
    </HelmetComponent>
  );
}

export default Homepage;
