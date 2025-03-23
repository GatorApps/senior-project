import Admin from './views/Admin/Admin';
import ErrorPage from './views/ErrorPage/ErrorPage';
import Homepage from './views/Homepage/Homepage';
import InitializeApp from './components/InitializeApp/InitializeApp';
import Settings from './views/Settings/Settings';
import RequireAuth from './components/RequireAuth/RequireAuth';
import { Routes, Route } from 'react-router-dom';
import GenericPage from './components/GenericPage/GenericPage';
import OpportunitySearchPage from './views/OpportunitySearchPage/OpportunitySearchPage';
import MyApplicationsPage from './views/MyApplicationsPage/MyApplicationsPage';
import LabDetailsPage from './views/LabDetailsPage/LabDetailsPage';
import PositionDetailsPage from './views/PositionDetailsPage/PositionDetailsPage';
import MyPostingsPage from './views/MyPostingsPage/MyPostingsPage';
import ApplicationManagementPage from './views/ApplicationManagementPage/ApplicationManagementPage';
import PostingEditorPage from './views/PostingEditorPage/PostingEditorPage';


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
          <Route path="/lab" element={<LabDetailsPage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/posting" element={<PositionDetailsPage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/postingEditor" element={<PostingEditorPage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/postingmanagement" element={<MyPostingsPage />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={[100001]} />}>
          <Route path="/applicationmanagement" element={<ApplicationManagementPage />} />
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