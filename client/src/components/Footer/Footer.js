import { Fragment } from "react"
import Box from "@mui/material/Box"
import Divider from "@mui/material/Divider"
import Grid from "@mui/material/Grid"
import Link from "@mui/material/Link"
import Typography from "@mui/material/Typography"
import FacebookIcon from "@mui/icons-material/Facebook"
import TwitterIcon from "@mui/icons-material/Twitter"
import InstagramIcon from "@mui/icons-material/Instagram"
import YouTubeIcon from "@mui/icons-material/YouTube"

// Footer data structure
const footerData = {
  columns: [
    {
      title: "RESOURCES",
      links: [
        { text: "ONE UF", url: "/" },
        { text: "WEBMAIL", url: "/" },
        { text: "MYUFL", url: "/" },
        { text: "E-LEARNING", url: "/" },
        { text: "DIRECTORY", url: "/" },
      ],
    },
    {
      title: "CAMPUS",
      links: [
        { text: "WEATHER", url: "/" },
        { text: "CAMPUS MAP", url: "/" },
        { text: "STUDENT TOURS", url: "/" },
        { text: "ACADEMIC CALENDAR", url: "/" },
        { text: "EVENTS", url: "/" },
      ],
    },
    {
      title: "WEBSITE",
      links: [
        { text: "CONTACT US", url: "/" },
        { text: "ABOUT", url: "/" },
        { text: "WEBSITE LISTING", url: "/" },
        { text: "ACCESSIBILITY", url: "/" },
        { text: "PRIVACY POLICY", url: "/" },
        { text: "REGULATIONS", url: "/" },
      ],
    },
  ],
  socialMedia: [
    { icon: <FacebookIcon />, url: "/", label: "Facebook" },
    { icon: <TwitterIcon />, url: "/", label: "Twitter" },
    { icon: <InstagramIcon />, url: "/", label: "Instagram" },
    { icon: <YouTubeIcon />, url: "/", label: "YouTube" },
  ],
  contactInfo: ["RESEARCH.UF by GatorApps", "© 2025 GatorApps. All rights reserved.", "All logos and trademarks are the property of their respective owners."],
}

// Reusable styles
const styles = {
  footerBox: {
    flexGrow: 1,
    margin: "24px auto auto",
    maxWidth: "1200px",
    padding: "20px",
  },
  columnTitle: {
    fontWeight: "500",
    marginBottom: 2,
  },
  linksContainer: {
    display: "flex",
    flexDirection: "column",
    gap: 1.5,
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
    alignItems: { xs: "center", md: "flex-start" }
  },
}

const Footer = () => {
  return (
    <Fragment>
      <Divider sx={{ marginTop: "60px" }} />
      <Box component="footer" sx={styles.footerBox}>
        <Grid container spacing={4}>
          {/* Dynamic Columns */}
          {footerData.columns.map((column, index) => (
            <Grid item xs={12} md={3} key={index}>
              <Typography variant="h6" sx={styles.columnTitle}>
                {column.title}
              </Typography>
              <Box sx={styles.linksContainer}>
                {column.links.map((link, linkIndex) => (
                  <Link
                    href={link.url}
                    color="primary"
                    underline="hover"
                    key={linkIndex}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    {link.text}
                  </Link>
                ))}
              </Box>
            </Grid>
          ))}

          {/* UF Logo and Contact Info */}
          <Grid item xs={12} md={3}>
            <Box sx={styles.rightColumn}>
              {/* UF Logo */}
              <Box sx={styles.logoContainer}>
                <img
                  src="https://static.gatorapps.org/garesearch/uf-logo-with-text.png"
                  alt="UF Logo"
                  style={{ width: "200px", height: "auto" }}
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
                <Typography variant="body2" key={index} marginY={0.5}>
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
