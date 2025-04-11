import React, { useState, useEffect, useCallback } from "react"
import { useSelector } from "react-redux"
import { Link, useLocation, useNavigate } from "react-router-dom"
import { debounce } from "lodash"

// MUI Components
import {
  Box,
  Button,
  Container,
  Paper,
  Typography,
  Autocomplete,
  TextField,
  InputAdornment,
  Pagination,
  Alert,
  Snackbar,
  CircularProgress,
  List,
  ListItem,
  Divider,
  Tooltip,
  IconButton,
  Link as MuiLink,
  useMediaQuery,
  useTheme,
} from "@mui/material"

// Icons
import SearchIcon from "@mui/icons-material/Search"
import OpenInNewIcon from "@mui/icons-material/OpenInNew"
import ApplyIcon from "@mui/icons-material/AssignmentTurnedIn"
import { styled } from "@mui/material/styles"

// Custom Components
import HelmetComponent from "../../components/HelmetComponent/HelmetComponent"
import Header from "../../components/Header/Header"
import Footer from "../../components/Footer/Footer"
import SkeletonGroup from "../../components/SkeletonGroup/SkeletonGroup"

// API
import { axiosPrivate } from "../../apis/backend"

// Styled components for the search results
const ResultItem = styled(ListItem)(({ theme }) => ({
  marginBottom: theme.spacing(1),
  borderRadius: theme.spacing(1),
  padding: theme.spacing(2),
  transition: "all 0.2s ease",
  "&:hover": {
    backgroundColor: theme.palette.action.hover,
  },
}))

// Custom styled divider with spacing
const StyledDivider = styled(Divider)(({ theme }) => ({
  marginTop: theme.spacing(1),
  marginBottom: theme.spacing(1),
}))

// Custom styled Typography for truncated titles - fixed to ensure ellipsis shows
const TruncatedTitle = styled(Typography)(({ theme }) => ({
  overflow: "hidden",
  textOverflow: "ellipsis",
  whiteSpace: "nowrap",
  width: "100%", // Take full width to ensure truncation works
  display: "block", // Block to ensure width constraint works
  color: theme.palette.primary.main,
  fontWeight: 500,
}))

// Title container to properly handle overflow
const TitleContainer = styled(Box)(({ theme }) => ({
  flexGrow: 1,
  flexShrink: 1,
  minWidth: 0, // This is crucial for text-overflow to work in a flex container
  overflow: "hidden", // Ensure the container doesn't expand with long content
  marginRight: theme.spacing(2), // Add space between title and buttons
  display: "flex",
  flexDirection: "column", // Stack title and timestamp vertically
  maxWidth: "calc(100% - 100px)", // Ensure there's space for buttons on desktop
  [theme.breakpoints.down("sm")]: {
    maxWidth: "100%", // On mobile, take full width since buttons are hidden
  },
}))

// Action buttons container with fixed width
const ActionButtonsContainer = styled(Box)(({ theme }) => ({
  display: "flex",
  flexShrink: 0, // Prevent the buttons from shrinking
  marginLeft: "auto",
  alignSelf: "flex-start", // Align to the top of the container
  [theme.breakpoints.down("sm")]: {
    display: "none", // Hide on mobile
  },
}))

// Styled container for search results - removed maxHeight and overflow
const ResultsContainer = styled(Box)(({ theme }) => ({
  padding: theme.spacing(1),
  position: "relative",
}))

// Styled pagination container
const PaginationContainer = styled(Box)(({ theme }) => ({
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  marginTop: theme.spacing(2),
  "& .MuiPagination-ul": {
    flexWrap: "nowrap",
  },
}))

// Styled search container wrapper for centering
const SearchContainerWrapper = styled(Box)(({ theme }) => ({
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  width: "100%",
  marginTop: "50px",
}))

// Styled search container
const SearchContainer = styled(Box)(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  gap: "16px",
  width: "70%", // Take 70% of the screen width on desktop
  [theme.breakpoints.down("sm")]: {
    width: "90%", // Take more width on mobile
    flexDirection: "column",
  },
}))

// Styled action button - updated to match PostingManagement.js styling and made larger
const ActionButton = styled(IconButton)(({ theme }) => ({
  border: "1px solid rgba(25, 118, 210, 0.5)",
  margin: theme.spacing(0, 0.5), // Reduced margin to bring buttons closer
  padding: theme.spacing(1.2), // Increased padding to make buttons larger
  transition: "all 0.2s ease",
  "&:hover": {
    backgroundColor: "rgba(25, 118, 210, 0.04)",
    border: "1px solid #1976d2",
  },
}))

// Styled link for "Search all positions"
const SearchAllLink = styled(MuiLink)(({ theme }) => ({
  fontSize: "1rem",
  color: theme.palette.text.secondary,
  marginTop: "18px",
  cursor: "pointer",
  textDecorationColor: "rgba(0, 0, 0, 0.3)",
  "&:hover": {
    textDecoration: "underline",
    textDecorationColor: "rgba(0, 0, 0, 0.5)",
  },
}))

// Title link wrapper to ensure proper truncation
const TitleLinkWrapper = styled(Box)(({ theme }) => ({
  width: "100%", // Take full width of parent
  overflow: "hidden", // Ensure overflow is hidden
}))

// Custom styled Link component to limit clickable area
const StyledLink = styled(Link)(({ theme }) => ({
  textDecoration: "none",
  display: "inline-block", // Use inline-block to limit clickable area
  maxWidth: "100%", // Ensure it doesn't exceed container width
  "&:hover": {
    textDecoration: "none",
  },
}))

const OpportunitySearch = ({ title }) => {
  // Redux state
  const userInfo = useSelector((state) => state.auth.userInfo)
  const theme = useTheme()
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"))

  // Component state
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [size, setSize] = useState(10) // Default to 10 results per page
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [totalCount, setTotalCount] = useState(0)
  const [positions, setPositions] = useState([])
  const [searchOptions, setSearchOptions] = useState([])
  const [searchText, setSearchText] = useState("")
  const [hasSearched, setHasSearched] = useState(false)
  const [showDropdown, setShowDropdown] = useState(true)
  const [snackbarOpen, setSnackbarOpen] = useState(false)
  const [snackbarMessage, setSnackbarMessage] = useState("")
  const [snackbarSeverity, setSnackbarSeverity] = useState("error")
  const [activeSearchTerm, setActiveSearchTerm] = useState("") // Track the active search term

  // Router hooks
  const location = useLocation()
  const navigate = useNavigate()
  const searchParams = new URLSearchParams(location.search)
  const searchQuery = searchParams.get("q")

  // Initialize search from URL query parameter but don't automatically search for all
  useEffect(() => {
    // Only search if searchQuery is explicitly defined (including empty string)
    if (searchQuery !== null) {
      setSearchText(searchQuery)
      setActiveSearchTerm(searchQuery)
      searchPositions(searchQuery, 0, size)
    }
    // If searchQuery is null, don't do anything - just show the initial page
  }, [searchQuery])

  // Search positions function - now allows empty search to get all opportunities
  const searchPositions = (text, currPage, currSize) => {
    // Hide dropdown when search is initiated
    setShowDropdown(false)
    setLoading(true)
    setError(null)
    setHasSearched(true)
    setActiveSearchTerm(text) // Update the active search term

    // Update URL with search query immediately
    const newSearchParams = new URLSearchParams(location.search)
    if (text.trim() === "") {
      // For empty searches, either remove the q parameter or set it to empty
      newSearchParams.set("q", "")
    } else {
      newSearchParams.set("q", text)
    }
    navigate(`${location.pathname}?${newSearchParams.toString()}`, { replace: true })

    axiosPrivate
      .get(`/posting/searchList?q=${encodeURIComponent(text)}&size=${currSize}&page=${currPage}`)
      .then((response) => {
        const payload = response.data.payload || {}
        setPositions(payload.positions || [])
        setTotalPages(payload.totalPages || 1)
        setTotalCount(payload.totalCount || 0)
      })
      .catch((error) => {
        console.error("Error fetching positions:", error)

        // Clear previous results on any error
        setPositions([])
        setTotalPages(0)
        setTotalCount(0)

        // Check if this is a "no results found" error
        const isNoResultsError =
          error.response?.status === 404 && error.response?.data?.errCode === "ERR_SEARCH_NO_RESULTS_FOUND"

        if (!isNoResultsError) {
          // Only set error for actual errors, not for "no results found"
          setError(error.response?.data?.message || error.message || "Failed to fetch positions")
          showSnackbar("Failed to fetch positions. Please try again.", "error")
        }
      })
      .finally(() => {
        setLoading(false)
      })
  }

  // Handle page change
  const handlePageChange = (newPage) => {
    setPage(newPage)
    // Use the active search term (from URL) instead of the current searchText
    // This ensures pagination works with the original search term
    searchPositions(activeSearchTerm, newPage, size)
    // Scroll to top of results
    window.scrollTo({ top: 0, behavior: "smooth" })
  }

  // Handle search button click - now allows empty searches
  const handleSearchButton = () => {
    setPage(0)
    searchPositions(searchText, 0, size)
  }

  // Handle search all positions
  const handleSearchAll = () => {
    setSearchText("")
    setPage(0)
    searchPositions("", 0, size)
  }

  // Debounced API call for search autocomplete
  const fetchSearchOptions = useCallback(
    debounce((query) => {
      if (!showDropdown || query.trim() === "") {
        setSearchOptions([])
        return
      }

      axiosPrivate
        .get(`/posting/searchIndexer?q=${encodeURIComponent(query)}`)
        .then((response) => {
          setSearchOptions(response.data.payload?.positions || [])
        })
        .catch((error) => {
          console.error("Error fetching search options:", error)
          setSearchOptions([])
        })
    }, 500),
    [showDropdown],
  )

  // Handle input change and trigger API search
  const handleSearchChange = (event, value) => {
    setSearchText(value)
    // Enable dropdown when user starts typing again
    setShowDropdown(true)
    fetchSearchOptions(value)
  }

  // Handle snackbar
  const showSnackbar = (message, severity = "error") => {
    setSnackbarMessage(message)
    setSnackbarSeverity(severity)
    setSnackbarOpen(true)
  }

  const handleSnackbarClose = () => {
    setSnackbarOpen(false)
  }

  // Handle keyboard events
  const handleKeyDown = (event) => {
    if (event.key === "Enter") {
      event.preventDefault()
      handleSearchButton()
    }
  }

  // Function to truncate HTML content for preview
  const truncateHtml = (html, maxLength = 200) => {
    if (!html) return "No description available."

    // Create a temporary div to parse HTML
    const tempDiv = document.createElement("div")
    tempDiv.innerHTML = html

    // Get text content
    let text = tempDiv.textContent || tempDiv.innerText || ""

    // Truncate text
    if (text.length > maxLength) {
      text = text.substring(0, maxLength) + "..."
    }

    return text
  }

  // Format date for display
  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleString()
    } catch (error) {
      return "N/A"
    }
  }

  // Check if title is likely to be truncated
  const isTitleLikelyTruncated = (title) => {
    return title && title.length > 40 // Adjust this threshold based on your layout
  }

  return (
    <HelmetComponent title="Search for Opportunities">
      <div className="GenericPage">
        <Header />
        <main>
          <Box>
            <Container maxWidth="lg">
              <Box className="GenericPage__container_title_box GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                <Box className="GenericPage__container_title_flexBox GenericPage__container_title_flexBox_left">
                  <Typography variant="h1">Search for Opportunities</Typography>
                </Box>
              </Box>
            </Container>

            <Container maxWidth="lg">
              <Paper className="GenericPage__container_paper" variant="outlined">
                {/* Search bar - now wrapped in a centered container */}
                <SearchContainerWrapper>
                  <SearchContainer>
                    <Autocomplete
                      freeSolo
                      open={showDropdown && searchOptions.length > 0}
                      sx={{
                        width: "100%",
                        maxWidth: "100%",
                        backgroundColor: "white",
                        "& .MuiOutlinedInput-root": {
                          borderRadius: "8px",
                          transition: "all 0.2s ease",
                          boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                          "&:hover": {
                            boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
                          },
                          "&.Mui-focused": {
                            boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                          },
                        },
                        // Increase font size for the input text
                        "& .MuiInputBase-input": {
                          fontSize: "1.1rem",
                        },
                      }}
                      value={searchText}
                      onChange={(event, newValue) => {
                        if (typeof newValue === "string") {
                          setSearchText(newValue)
                        } else if (newValue && newValue.positionName) {
                          setSearchText(newValue.positionName)
                        } else {
                          setSearchText("")
                        }
                      }}
                      onInputChange={handleSearchChange}
                      options={searchOptions}
                      getOptionLabel={(option) => (typeof option === "string" ? option : option.positionName || "")}
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          variant="outlined"
                          size="small"
                          placeholder="Search for opportunities..."
                          sx={{
                            ...params.sx,
                            height: "55px", // Restore to original height
                            "& .MuiInputBase-root": {
                              height: "100%",
                            },
                          }}
                          InputProps={{
                            ...params.InputProps,
                            startAdornment: (
                              <InputAdornment position="start" sx={{ ml: 1 }}>
                                <SearchIcon color="action" fontSize="medium" sx={{ fontSize: "1.5rem" }} />
                              </InputAdornment>
                            ),
                          }}
                          onKeyDown={handleKeyDown}
                        />
                      )}
                    />
                    <Button
                      variant="contained"
                      size="medium"
                      onClick={handleSearchButton}
                      disabled={loading}
                      sx={{
                        minWidth: { xs: "100%", sm: "100px" },
                        height: "40px", // Match the height from the attachment
                        fontSize: "0.9rem",
                      }}
                    >
                      {loading ? <CircularProgress size={22} color="inherit" /> : "Search"}
                    </Button>
                  </SearchContainer>

                  {/* Search all positions link */}
                  <SearchAllLink onClick={handleSearchAll}>Browse all opportunities</SearchAllLink>
                </SearchContainerWrapper>

                {/* Error message - only show for actual errors, not for "no results" */}
                {error && (
                  <Box margin="30px">
                    <Alert severity="error" onClose={() => setError(null)}>
                      {error}
                    </Alert>
                  </Box>
                )}

                {/* Search results */}
                <ResultsContainer margin="30px">
                  {loading ? (
                    <>
                      <SkeletonGroup />
                      <SkeletonGroup />
                      <SkeletonGroup />
                    </>
                  ) : positions.length > 0 ? (
                    <List sx={{ width: "100%" }}>
                      {positions.map((position, index) => (
                        <React.Fragment key={position.positionId || index}>
                          <ResultItem>
                            <Box sx={{ width: "100%" }}>
                              {/* Lab name */}
                              <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 0.5 }}>
                                {position.labName || "Unknown Lab"}
                              </Typography>

                              {/* Position title with action buttons - Improved layout with separate columns */}
                              <Box sx={{ display: "flex", alignItems: "flex-start", width: "100%" }}>
                                <TitleContainer>
                                  <Tooltip
                                    title={position.positionName || "Untitled Position"}
                                    arrow
                                    disableHoverListener={!isTitleLikelyTruncated(position.positionName)}
                                  >
                                    <TitleLinkWrapper>
                                      {/* Use a styled Link component with proper truncation */}
                                      <StyledLink to={`/posting?postingId=${position.positionId}`} target="_blank">
                                        <TruncatedTitle variant="h6">
                                          {position.positionName || "Untitled Position"}
                                        </TruncatedTitle>
                                      </StyledLink>
                                    </TitleLinkWrapper>
                                  </Tooltip>

                                  {/* Posted timestamp - moved under the title */}
                                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                                    Posted: {formatDate(position.postedTimeStamp)}
                                  </Typography>
                                </TitleContainer>

                                {/* Action buttons in their own column - hidden on mobile */}
                                <ActionButtonsContainer>
                                  <Tooltip title="Apply" arrow>
                                    <ActionButton
                                      component={Link}
                                      to={`/posting?postingId=${position.positionId}&apply=1`}
                                      target="_blank"
                                      size="medium"
                                      color="primary"
                                      aria-label="Apply"
                                      sx={{ mr: 0.5 }}
                                    >
                                      <ApplyIcon />
                                    </ActionButton>
                                  </Tooltip>

                                  <Tooltip title="View details" arrow>
                                    <ActionButton
                                      component={Link}
                                      to={`/posting?postingId=${position.positionId}`}
                                      target="_blank"
                                      size="medium"
                                      color="primary"
                                      aria-label="View details"
                                    >
                                      <OpenInNewIcon />
                                    </ActionButton>
                                  </Tooltip>
                                </ActionButtonsContainer>
                              </Box>

                              {/* Position description preview */}
                              <Typography
                                variant="body2"
                                color="text.secondary"
                                sx={{
                                  display: "-webkit-box",
                                  WebkitLineClamp: 3,
                                  WebkitBoxOrient: "vertical",
                                  overflow: "hidden",
                                  textOverflow: "ellipsis",
                                  mt: 1,
                                }}
                              >
                                {truncateHtml(position.positionDescription)}
                              </Typography>
                            </Box>
                          </ResultItem>
                          <StyledDivider component="li" />
                        </React.Fragment>
                      ))}
                    </List>
                  ) : hasSearched ? (
                    <Box
                      sx={{
                        textAlign: "center",
                        py: 4,
                      }}
                    >
                      <Typography variant="h6" color="text.secondary" gutterBottom>
                        No positions found
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Try adjusting your search terms or browse all opportunities
                      </Typography>
                    </Box>
                  ) : (
                    <Box
                      sx={{
                        textAlign: "center",
                        py: 4,
                      }}
                    >
                      <Typography variant="h6" color="text.secondary" gutterBottom>
                        Enter a search term or browse all opportunities
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Search by keywords, lab name, or position title
                      </Typography>
                    </Box>
                  )}
                </ResultsContainer>

                {/* Pagination Controls */}
                {totalCount > 0 && (
                  <PaginationContainer sx={{ mb: 3 }}>
                    {/* Display Total Results */}
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                      {`Displaying ${totalCount > 0 ? Math.min(page * size + 1, totalCount) : 0}-${Math.min((page + 1) * size, totalCount)} of ${totalCount} results`}
                    </Typography>

                    {/* Pagination Controls - always show */}
                    <Pagination
                      count={totalPages}
                      page={page + 1} // Adjust to 1-based pagination
                      onChange={(event, newPage) => handlePageChange(newPage - 1)}
                      color="primary"
                      showFirstButton
                      showLastButton
                      size="medium"
                      siblingCount={1}
                    />
                  </PaginationContainer>
                )}
              </Paper>
            </Container>
          </Box>
        </main>
        <Footer />

        {/* Snackbar for notifications */}
        <Snackbar
          open={snackbarOpen}
          autoHideDuration={6000}
          onClose={handleSnackbarClose}
          anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
        >
          <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: "100%" }}>
            {snackbarMessage}
          </Alert>
        </Snackbar>
      </div>
    </HelmetComponent>
  )
}

export default OpportunitySearch
