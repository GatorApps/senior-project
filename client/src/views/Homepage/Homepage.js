import { useState, useEffect } from "react"
import Box from "@mui/material/Box"
import HelmetComponent from "../../components/HelmetComponent/HelmetComponent"
import Header from "../../components/Header/Header"
import Footer from "../../components/Footer/Footer"
import { useSelector } from "react-redux"
import OpportunitySearchPageCard from "../OpportunitySearch/OpportunitySearchCard"
import MyApplicationsCard from "../../views/MyApplications/MyApplicationsCard"
import MyPostingsPageCard from "../../views/MyPostingsPage/MyPostingsPageCard"
import ApplicationManagementCard from "../../views/ApplicationManagement/ApplicationManagementCard"
import MessagesPageCard from "../Messages/MessagesCard"
import Tabs from "@mui/material/Tabs"
import Tab from "@mui/material/Tab"
import Alert from "../../components/Alert/Alert" // Import the Alert component

const Homepage = () => {
  const userInfo = useSelector((state) => state.auth.userInfo)
  // userInfo?.roles?.includes(500301) for faculty/staff
  // for student, its 500201

  const [view, setView] = useState(null)
  const [numColumns, setNumColumns] = useState(3)

  const handleChange = (event, newValue) => {
    setView(newValue)
  }

  const updateColumns = () => {
    if (window.innerWidth < 825) {
      setNumColumns(1)
    } else if (window.innerWidth < 1200) {
      setNumColumns(2)
    } else {
      setNumColumns(3)
    }
  }

  // Initialize view based on user roles
  useEffect(() => {
    if (userInfo?.roles) {
      const hasStudentRole = userInfo.roles.includes(500201)
      const hasFacultyRole = userInfo.roles.includes(500301)

      // Set default view based on roles
      if (hasStudentRole) {
        // If user has student role, default to student view (0)
        setView(0)
      } else if (hasFacultyRole) {
        // If user only has faculty role, default to faculty view (0 since it's the only tab)
        setView(0)
      }
    }
  }, [userInfo])

  useEffect(() => {
    updateColumns()
    window.addEventListener("resize", updateColumns)
    return () => window.removeEventListener("resize", updateColumns)
  }, [])

  // Handle query string parameter 't' on component mount
  useEffect(() => {
    if (!userInfo?.roles) return

    const searchParams = new URLSearchParams(window.location.search)
    const tabParam = searchParams.get("t")

    if (tabParam) {
      const hasStudentRole = userInfo.roles.includes(500201)
      const hasFacultyRole = userInfo.roles.includes(500301)

      // Set view based on query parameter
      if (tabParam.toLowerCase() === "faculty" && hasFacultyRole) {
        // If faculty tab is requested and user has faculty role
        const facultyTabIndex = hasStudentRole ? 1 : 0
        setView(facultyTabIndex)
      } else if (tabParam.toLowerCase() === "student" && hasStudentRole) {
        // If student tab is requested and user has student role
        setView(0)
      }

      // Remove query parameter from URL without reloading the page
      const newUrl = window.location.pathname + window.location.hash
      window.history.replaceState({}, document.title, newUrl)
    }
  }, [userInfo])

  // Check if user has neither student nor faculty role
  const hasNoRequiredRoles = userInfo?.roles && !userInfo.roles.includes(500201) && !userInfo.roles.includes(500301)

  // If user has no required roles, show the access denied alert
  if (hasNoRequiredRoles) {
    return (
      <HelmetComponent>
        <div className="Homepage">
          <Header />
          <main>
            <Box sx={{ maxWidth: "800px", margin: "48px auto", padding: "0 16px" }}>
              <Alert
                data={{
                  severity: "info",
                  title: "You Need Access",
                  message:
                    "Your account does not currently have access to this app. Please contact us at support@gatorapps.org to request access.",
                }}
                style={{
                  titleFontSize: "20px",
                  textFontSize: "16px",
                }}
              />
            </Box>
          </main>
        </div>
      </HelmetComponent>
    )
  }

  // If view is null (not yet initialized), don't render anything
  if (view === null) return null

  const studentCards = [
    <OpportunitySearchPageCard key="opportunitySearch" />,
    <MyApplicationsCard key="myApplications" />,
    <MessagesPageCard key="messagesStudent" />,
  ]

  const facultyCards = [
    <MyPostingsPageCard key="myPostings" />,
    <ApplicationManagementCard key="applicationManagement" />,
    <MessagesPageCard key="messagesFaculty" />,
  ]

  // Determine which cards to show based on the selected view and available roles
  let cards
  if (userInfo?.roles?.includes(500201) && userInfo?.roles?.includes(500301)) {
    // User has both roles, use view to determine which cards to show
    cards = view === 0 ? studentCards : facultyCards
  } else if (userInfo?.roles?.includes(500201)) {
    // User only has student role
    cards = studentCards
  } else if (userInfo?.roles?.includes(500301)) {
    // User only has faculty role
    cards = facultyCards
  } else {
    // Fallback if no roles (shouldn't happen)
    cards = []
  }

  const columns = Array.from({ length: numColumns }, () => [])

  cards.forEach((card, index) => {
    columns[index % numColumns].push(card)
  })

  return (
    <HelmetComponent>
      <div className="Homepage">
        <Header />
        <main>
          {/* // General content displayed regardless of auth status  */}
          <div></div>

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
              width: "100%",
              backgroundColor: "#ffffff", // White background
              borderBottom: "1px solid #e0e0e0",
            }}
          >
            <Box
              sx={{
                maxWidth: "1200px",
                margin: "0 auto",
                padding: "0 16px",
              }}
            >
              <Tabs
                value={view}
                onChange={handleChange}
                aria-label="view selector"
                variant="standard"
                sx={{
                  "& .MuiTabs-indicator": {
                    backgroundColor: "#FA4616", // UF orange color for the indicator
                    height: "2px",
                  },
                  "& .MuiTabs-flexContainer": {
                    justifyContent: "flex-start",
                  },
                  "& .MuiTab-root": {
                    color: "#333333", // Dark text for tabs
                    fontSize: "16px",
                    fontWeight: 400,
                    textTransform: "none",
                    padding: "12px 16px",
                    minWidth: "120px",
                    "&.Mui-selected": {
                      color: "rgb(40, 87, 151)", // UF blue color for active tab
                      fontWeight: 500,
                    },
                  },
                }}
              >
                {userInfo?.roles?.includes(500201) && ( // Check if user is a student
                  // Only show Student View tab if user is a student
                  <Tab label="Student" />
                )}
                {userInfo?.roles?.includes(500301) && ( // Check if user is faculty/staff
                  // Only show Faculty View tab if user is faculty/staff
                  <Tab label="Faculty / Staff" />
                )}
              </Tabs>
            </Box>
          </Box>
          <Box
            component="workspace"
            sx={{
              display: "flex",
              gap: "24px",
              justifyContent: "center",
              margin: "36px auto 24px auto",
              padding: "0 16px",
              maxWidth: "1200px",
            }}
          >
            {columns.map((column, colIndex) => (
              <Box key={colIndex} sx={{ flex: 1, minWidth: "300px" }}>
                {column.map((card, cardIndex) => (
                  <Box key={cardIndex} sx={{ width: "380px", marginBottom: "24px" }}>
                    {card}
                  </Box>
                ))}
              </Box>
            ))}
          </Box>
        </main>
        <Footer />
      </div>
    </HelmetComponent>
  )
}

export default Homepage
