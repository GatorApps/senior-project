import HelmetComponent from '../../components/HelmetComponent/HelmetComponent';
import Header from '../../components/Header/Header.js';

const Admin = () => {
  return (
    <HelmetComponent title={"Admin"}>
      <div className="AdminPage">
        <Header />
        <div>
          <div>Welcome to admin page</div>
        </div>
      </div>
    </HelmetComponent>
  );
}

export default Admin;
