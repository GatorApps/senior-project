import { Fragment, useState } from "react"
import Box from "@mui/material/Box"
import Divider from "@mui/material/Divider"
import Grid from "@mui/material/Grid"
import Link from "@mui/material/Link"
import Typography from "@mui/material/Typography"
import FacebookIcon from "@mui/icons-material/Facebook"
import TwitterIcon from "@mui/icons-material/Twitter"
import InstagramIcon from "@mui/icons-material/Instagram"
import YouTubeIcon from "@mui/icons-material/YouTube"
import Accordion from "@mui/material/Accordion"
import AccordionSummary from "@mui/material/AccordionSummary"
import AccordionDetails from "@mui/material/AccordionDetails"
import ExpandMoreIcon from "@mui/icons-material/ExpandMore"
import useMediaQuery from "@mui/material/useMediaQuery"
import { useTheme } from "@mui/material/styles"

// Footer data structure
const footerData = {
  columns: [
    {
      title: "RESOURCES",
      links: [
        { text: "ONE UF", url: "https://one.uf.edu" },
        { text: "WEBMAIL", url: "https://webmail.ufl.edu" },
        { text: "MYUFL", url: "https://my.ufl.edu" },
        { text: "E-LEARNING", url: "http://elearning.ufl.edu" },
        { text: "DIRECTORY", url: "https://directory.ufl.edu" },
      ],
    },
    {
      title: "CAMPUS",
      links: [
        { text: "WEATHER", url: "http://ufweather.org" },
        { text: "CAMPUS MAP", url: "http://campusmap.ufl.edu" },
        { text: "STUDENT TOURS", url: "http://virtualtour.ufl.edu" },
        { text: "ACADEMIC CALENDAR", url: "https://catalog.ufl.edu/ugrad/current/Pages/dates-and-deadlines.aspx" },
        { text: "EVENTS", url: "http://calendar.ufl.edu" },
      ],
    },
    {
      title: "WEBSITE",
      links: [
        // { text: "CONTACT US", url: "/" },
        // { text: "ABOUT", url: "/" },
        { text: "WEBSITE LISTING", url: "http://www.ufl.edu/websites" },
        { text: "ACCESSIBILITY", url: "https://accessibility.ufl.edu" },
        // { text: "PRIVACY POLICY", url: "/" },
        // { text: "REGULATIONS", url: "/" },
      ],
    },
  ],
  socialMedia: [
    { icon: <FacebookIcon />, url: "https://www.facebook.com/uflorida", label: "Facebook" },
    { icon: <TwitterIcon />, url: "https://x.com/UF", label: "X (formerly Twitter)" },
    { icon: <InstagramIcon />, url: "https://www.instagram.com/uflorida", label: "Instagram" },
    { icon: <YouTubeIcon />, url: "https://www.youtube.com/user/universityofflorida", label: "YouTube" },
  ],
  contactInfo: [
    "RESEARCH.UF by GatorApps",
    "© 2025 GatorApps. All rights reserved.",
    "All logos and trademarks are the property of their respective owners.",
  ],
}

// Reusable styles
const styles = {
  footerBox: {
    flexGrow: 1,
    margin: "24px auto auto",
    maxWidth: "960px",
    padding: "20px",
  },
  columnTitle: {
    fontWeight: "500",
    marginBottom: 2,
    fontSize: "0.95rem",
  },
  linksContainer: {
    display: "flex",
    flexDirection: "column",
    gap: 1.5,
  },
  linkText: {
    fontSize: "0.8rem",
  },
  socialIcon: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    width: 40,
    height: 40,
    borderRadius: "50%",
    border: "2px solid rgb(224, 129, 46)",
    color: "rgb(224, 129, 46)",
  },
  socialContainer: {
    display: "flex",
    gap: 1,
    marginBottom: 2,
  },
  logoContainer: {
    marginBottom: 2,
  },
  rightColumn: {
    display: "flex",
    flexDirection: "column",
    alignItems: { xs: "center", md: "flex-start" },
  },
  accordion: {
    boxShadow: "none",
    "&:before": {
      display: "none",
    },
    backgroundColor: "transparent",
  },
  accordionSummary: {
    padding: 0,
    minHeight: "48px",
    "& .MuiAccordionSummary-content": {
      margin: "12px 0",
    },
  },
  accordionDetails: {
    padding: "0 0 16px 0",
  },
}

const Footer = () => {
  const theme = useTheme()
  const isMobile = useMediaQuery(theme.breakpoints.down("md"))
  const [expanded, setExpanded] = useState(false)

  const handleChange = (panel) => (event, isExpanded) => {
    setExpanded(isExpanded ? panel : false)
  }

  // Links component to be reused
  const LinksComponent = ({ links }) => (
    <Box sx={styles.linksContainer}>
      {links.map((link, linkIndex) => (
        <Link
          href={link.url}
          color="primary"
          underline="hover"
          key={linkIndex}
          target="_blank"
          rel="noopener noreferrer"
          sx={styles.linkText}
        >
          {link.text}
        </Link>
      ))}
    </Box>
  )

  return (
    <Fragment>
      <Divider sx={{ marginTop: "60px" }} />
      <Box component="footer" sx={styles.footerBox}>
        <Grid container spacing={3} sx={{ marginBottom: "14px" }}>
          {/* Dynamic Columns - Desktop or Mobile */}
          {isMobile ? (
            // Mobile view with accordions
            <Grid item xs={12}>
              {footerData.columns.map((column, index) => (
                <Accordion
                  key={index}
                  expanded={expanded === `panel${index}`}
                  onChange={handleChange(`panel${index}`)}
                  sx={styles.accordion}
                  disableGutters
                >
                  <AccordionSummary
                    expandIcon={<ExpandMoreIcon fontSize="small" />}
                    aria-controls={`panel${index}-content`}
                    id={`panel${index}-header`}
                    sx={styles.accordionSummary}
                  >
                    <Typography variant="subtitle1" sx={styles.columnTitle}>
                      {column.title}
                    </Typography>
                  </AccordionSummary>
                  <AccordionDetails sx={styles.accordionDetails}>
                    <LinksComponent links={column.links} />
                  </AccordionDetails>
                </Accordion>
              ))}
            </Grid>
          ) : (
            // Desktop view with columns
            footerData.columns.map((column, index) => (
              <Grid item xs={12} md={2.76} key={index}>
                <Typography variant="h6" sx={styles.columnTitle}>
                  {column.title}
                </Typography>
                <LinksComponent links={column.links} />
              </Grid>
            ))
          )}

          {/* UF Logo and Contact Info */}
          <Grid item xs={12} md={3.7}>
            <Box sx={styles.rightColumn}>
              {/* UF Logo */}
              <Box sx={styles.logoContainer}>
                <img
                  src="https://static.gatorapps.org/garesearch/uf-logo-with-text.png"
                  alt="UF Logo"
                  style={{ width: "180px", height: "auto" }}
                />
              </Box>

              {/* Social Media Icons */}
              <Box sx={styles.socialContainer}>
                {footerData.socialMedia.map((item, index) => (
                  <Link href={item.url} aria-label={item.label} key={index} target="_blank" rel="noopener noreferrer">
                    <Box sx={styles.socialIcon}>{item.icon}</Box>
                  </Link>
                ))}
              </Box>

              {/* Contact Info */}
              {footerData.contactInfo.map((line, index) => (
                <Typography variant="body2" key={index} marginY={0.5} lineHeight={1.25}>
                  {line}
                </Typography>
              ))}
            </Box>
          </Grid>
        </Grid>
      </Box>
    </Fragment>
  )
}

export default Footer
