import Admin from './views/Admin/Admin';
import ErrorPage from './views/ErrorPage/ErrorPage';
import Homepage from './views/Homepage/Homepage';
import InitializeApp from './components/InitializeApp/InitializeApp';
import Settings from './views/Settings/Settings';
import RequireAuth from './components/RequireAuth/RequireAuth';
import { Routes, Route } from 'react-router-dom';
import GenericPage from './components/GenericPage/GenericPage';
import OpportunitySearchPage from './views/OpportunitySearch/OpportunitySearch';
import MyApplicationsPage from './views/MyApplications/MyApplications';
import LabDetails from './views/LabDetails/LabDetails';
import PositionDetailsPage from './views/PositionDetails/PositionDetails';
import PostingManagement from './views/PostingManagement/PostingManagement';
import ApplicationManagement from './views/ApplicationManagement/ApplicationManagement';
import PostingEditor from './views/PostingEditor/PostingEditor';
import MessagesPage from './views/Messages/Messages';

function App() {
  return (
    <Routes>
      <Route element={<InitializeApp />}>
        {/* public routes */}
        <Route path="/genericpage" element={<GenericPage />} />

        {/* protected routes */}
        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/" element={<Homepage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/search" element={<OpportunitySearchPage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/myapplications" element={<MyApplicationsPage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/lab" element={<LabDetails />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/posting" element={<PositionDetailsPage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/postingeditor" element={<PostingEditor />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/postingmanagement" element={<PostingManagement />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/applicationmanagement" element={<ApplicationManagement />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/messages" element={<MessagesPage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100999]} />}>
          <Route path="/admin" element={<Admin />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/settings" element={<Settings />} />
        </Route>

        {/* catch all */}
        <Route path="*" element={<ErrorPage error="404" />} />
      </Route>
    </Routes>
  );
}

export default App;