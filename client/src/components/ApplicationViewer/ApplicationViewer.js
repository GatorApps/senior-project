import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Tabs,
  Tab,
  CircularProgress,
  Alert,
  IconButton,
  Tooltip
} from '@mui/material';
import { Download as DownloadIcon } from '@mui/icons-material';
import { axiosPrivate } from '../../apis/backend';
import { Document, Page, pdfjs } from 'react-pdf';
import 'react-pdf/dist/esm/Page/TextLayer.css';
import 'react-pdf/dist/esm/Page/AnnotationLayer.css';

// IMPORTANT: Use the existing worker version that we know works
pdfjs.GlobalWorkerOptions.workerSrc = `https://unpkg.com/pdfjs-dist@2.16.105/build/pdf.worker.min.js`;

const ApplicationViewer = ({ application, applicantInfo }) => {
  const [tabValue, setTabValue] = useState(0);
  const [numPages, setNumPages] = useState(null);
  const [pdfUrl, setPdfUrl] = useState(null);
  const [loading, setLoading] = useState(false);
  const [appLoading, setAppLoading] = useState(false);
  const [error, setError] = useState(null);
  const [supplementalResponses, setSupplementalResponses] = useState('');
  const [resumeId, setResumeId] = useState(null);
  const [transcriptId, setTranscriptId] = useState(null);
  const [useIframe, setUseIframe] = useState(false);

  // Fetch application details when application prop changes
  useEffect(() => {
    if (!application) return;

    setAppLoading(true);
    setError(null);

    axiosPrivate.get(`/application/application?labId=${application.labId}&applicationId=${application.applicationId}`)
      .then((response) => {
        if (response.data && response.data.errCode === '0' && response.data.payload.application) {
          const app = response.data.payload.application;
          setSupplementalResponses(app.supplementalResponses || 'No supplemental responses provided.');
          setResumeId(app.resumeId);
          setTranscriptId(app.transcriptId);
        } else {
          setError('No application data found or server returned an error.');
        }
      })
      .catch((error) => {
        console.error('Error fetching application:', error);
        setError(error.response?.data?.message || error.message || 'Error fetching application details');
      })
      .finally(() => {
        setAppLoading(false);
      });
  }, [application]);

  // Fetch PDF file
  const fetchPdf = async (fileId) => {
    if (!fileId) {
      setError('No file ID provided');
      return;
    }

    setLoading(true);
    setError(null);
    setPdfUrl(null);
    setUseIframe(false);
    setNumPages(null);

    try {
      const response = await axiosPrivate.get(`/file/${fileId}`, {
        responseType: 'blob',
      });

      if (response.status !== 200) {
        throw new Error(`Failed to fetch PDF. Server responded with status: ${response.status}`);
      }

      if (!response.data || !(response.data instanceof Blob)) {
        throw new Error('Invalid PDF file received.');
      }

      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = URL.createObjectURL(blob);
      setPdfUrl(url);
    } catch (error) {
      console.error('Error fetching PDF:', error);
      setError(error.response?.data?.message || error.message || 'Failed to load PDF. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Fetch PDF when tab changes
  useEffect(() => {
    if (tabValue === 0 && resumeId) {
      fetchPdf(resumeId);
    } else if (tabValue === 1 && transcriptId) {
      fetchPdf(transcriptId);
    } else if (tabValue < 2) {
      // Clear PDF if we're on a PDF tab but don't have the file ID
      setPdfUrl(null);
    }

    // Cleanup function to revoke object URL
    return () => {
      if (pdfUrl) {
        URL.revokeObjectURL(pdfUrl);
      }
    };
  }, [tabValue, resumeId, transcriptId]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const onDocumentLoadSuccess = ({ numPages }) => {
    setNumPages(numPages);
  };

  // Handle PDF loading error
  const onDocumentLoadError = (error) => {
    console.error('Error loading PDF document with react-pdf:', error);
    // Fall back to iframe
    setUseIframe(true);
  };

  // Generate filename for download
  const getDownloadFilename = () => {
    const { firstName, lastName } = applicantInfo || {};
    const fileType = tabValue === 0 ? 'resume' : 'transcript';

    if (firstName && lastName) {
      return `${firstName.toLowerCase()}_${lastName.toLowerCase()}_${fileType}.pdf`;
    }

    return `${fileType}.pdf`;
  };

  // Handle download click
  const handleDownload = (e) => {
    e.stopPropagation(); // Prevent tab change when clicking download
  };

  if (!application) return null;

  return (
    <Box sx={{
      display: 'flex',
      height: '100%',
      overflow: 'hidden'
    }}>
      {/* Vertical tabs on the left with smaller margins */}
      <Box sx={{
        width: 180,
        borderRight: 1,
        borderColor: 'divider',
        backgroundColor: '#f5f5f5'
      }}>
        <Tabs
          orientation="vertical"
          value={tabValue}
          onChange={handleTabChange}
          sx={{
            borderRight: 1,
            borderColor: 'divider',
            '& .MuiTab-root': {
              alignItems: 'flex-start',
              textAlign: 'left',
              pl: 2,
              pr: 1,
              minHeight: 48,
              py: 1,
              position: 'relative', // For positioning the download button
              transition: 'background-color 0.2s ease'
            },
            // Add background color to the selected tab
            '& .Mui-selected': {
              backgroundColor: 'rgba(25, 118, 210, 0.08)', // Light blue background for selected tab
              fontWeight: 500
            }
          }}
          indicatorColor="primary"
          textColor="primary"
        >
          <Tab
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                <span>Resume</span>
                {tabValue === 0 && pdfUrl && (
                  <Tooltip title="Download Resume">
                    <IconButton
                      component="a"
                      href={pdfUrl}
                      download={getDownloadFilename()}
                      onClick={handleDownload}
                      size="small"
                      sx={{
                        ml: 1,
                        p: 0.5,
                        position: 'absolute',
                        right: 8
                      }}
                    >
                      <DownloadIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                )}
              </Box>
            }
            disabled={!resumeId}
          />
          <Tab
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                <span>Transcript</span>
                {tabValue === 1 && pdfUrl && (
                  <Tooltip title="Download Transcript">
                    <IconButton
                      component="a"
                      href={pdfUrl}
                      download={getDownloadFilename()}
                      onClick={handleDownload}
                      size="small"
                      sx={{
                        ml: 1,
                        p: 0.5,
                        position: 'absolute',
                        right: 8
                      }}
                    >
                      <DownloadIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                )}
              </Box>
            }
            disabled={!transcriptId}
          />
          <Tab label="Supplemental Responses" />
        </Tabs>
      </Box>

      {/* Content area on the right */}
      <Box sx={{
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: '#ffffff',
        position: 'relative'
      }}>
        {/* Content with scrolling */}
        <Box sx={{
          flex: 1,
          overflowY: 'auto',
          p: (tabValue === 0 || tabValue === 1) && pdfUrl ? (useIframe ? 0 : 2) : 3
        }}>
          {appLoading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
              <CircularProgress />
            </Box>
          ) : error ? (
            <Alert severity="error" sx={{ m: 2 }}>
              {error}
            </Alert>
          ) : (
            <>
              {tabValue === 2 && (
                <Box
                  sx={{
                    p: 2,
                    border: '1px solid #e0e0e0',
                    borderRadius: '4px',
                    backgroundColor: '#ffffff',
                    overflowY: 'auto',
                    height: '100%'
                  }}
                  dangerouslySetInnerHTML={{ __html: supplementalResponses }}
                />
              )}

              {(tabValue === 0 || tabValue === 1) && renderPdfContent()}
            </>
          )}
        </Box>
      </Box>
    </Box>
  );

  // Helper function to render PDF content
  function renderPdfContent() {
    if (loading) {
      return (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
          <Typography sx={{ ml: 2 }}>Loading document...</Typography>
        </Box>
      );
    }

    if (error && !pdfUrl) {
      return (
        <Box>
          <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>
        </Box>
      );
    }

    if (!pdfUrl) {
      const fileType = tabValue === 0 ? 'resume' : 'transcript';
      const fileId = tabValue === 0 ? resumeId : transcriptId;

      if (!fileId) {
        return (
          <Alert severity="info">
            No {fileType} was uploaded with this application.
          </Alert>
        );
      }

      return (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
        </Box>
      );
    }

    // Use iframe if react-pdf failed or if we're forcing iframe
    if (useIframe) {
      return (
        <Box sx={{ height: '100%', width: '100%' }}>
          <iframe
            src={pdfUrl}
            title={tabValue === 0 ? "Resume" : "Transcript"}
            width="100%"
            height="100%"
            style={{ border: 'none' }}
          />
        </Box>
      );
    }

    // Try to render with react-pdf
    return (
      <Box sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        width: '100%',
        overflow: 'auto'
      }}>
        <Document
          file={pdfUrl}
          onLoadSuccess={onDocumentLoadSuccess}
          onLoadError={onDocumentLoadError}
          loading={
            <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
              <CircularProgress />
              <Typography sx={{ ml: 2 }}>Loading document...</Typography>
            </Box>
          }
        >
          {numPages && Array.from(new Array(numPages), (el, index) => (
            <Box key={`page_${index + 1}`} sx={{ mb: 2, boxShadow: '0 2px 5px rgba(0,0,0,0.1)' }}>
              <Page
                pageNumber={index + 1}
                renderTextLayer={true}
                renderAnnotationLayer={true}
                width={Math.min(600, window.innerWidth - 250)} // Responsive width
                onRenderError={() => {
                  console.error(`Error rendering page ${index + 1}`);
                  if (index === 0) setUseIframe(true);
                }}
              />
            </Box>
          ))}
        </Document>

        {numPages > 0 && (
          <Box sx={{ mt: 2, mb: 2, display: 'flex', justifyContent: 'center' }}>
            <Typography>
              {numPages} page{numPages > 1 ? 's' : ''}
            </Typography>
          </Box>
        )}
      </Box>
    );
  }
};

export default ApplicationViewer;