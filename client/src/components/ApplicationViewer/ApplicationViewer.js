import React, { useState, useEffect, useRef, useCallback } from 'react';
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

// Flag to track if PDF.js worker configuration failed
let pdfWorkerConfigFailed = false;

// Configure PDF.js worker
try {
  pdfjs.GlobalWorkerOptions.workerSrc = new URL(
    'pdfjs-dist/build/pdf.worker.mjs',
    import.meta.url
  ).toString();
} catch (error) {
  console.error('Error configuring PDF.js worker:', error);
  // Mark that worker config failed - we'll use iframe instead
  pdfWorkerConfigFailed = true;
}

// Error boundary component to catch react-pdf rendering errors
class PDFErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error('PDF Error Boundary caught an error:', error, errorInfo);
    if (this.props.onError) {
      this.props.onError(error);
    }
  }

  render() {
    if (this.state.hasError) {
      return this.props.fallback || null;
    }
    return this.props.children;
  }
}

const ApplicationViewer = ({ application, applicantInfo, previewMode = false, previewData = {} }) => {
  const [tabValue, setTabValue] = useState(0);
  const [numPages, setNumPages] = useState(null);
  const [pdfUrl, setPdfUrl] = useState(null);
  const [loading, setLoading] = useState(false);
  const [appLoading, setAppLoading] = useState(false);
  const [error, setError] = useState(null);
  const [supplementalResponses, setSupplementalResponses] = useState('');
  const [resumeId, setResumeId] = useState(null);
  const [transcriptId, setTranscriptId] = useState(null);
  const [useIframe, setUseIframe] = useState(pdfWorkerConfigFailed); // Initialize based on worker config status
  const pdfTimeoutRef = useRef(null);
  const previousPdfUrlRef = useRef(null);
  const fetchedRef = useRef(false);

  // Stabilize application object reference to prevent infinite loops
  const applicationRef = useRef(application);
  const previewModeRef = useRef(previewMode);
  const previewDataRef = useRef(previewData);

  // Update refs when props change
  useEffect(() => {
    applicationRef.current = application;
    previewModeRef.current = previewMode;
    previewDataRef.current = previewData;
  }, [application, previewMode, previewData]);

  // Fetch application details when application prop changes or use preview data
  useEffect(() => {
    // Skip if no application or if we've already fetched for this application
    if (!application) return;

    // Reset the fetched flag when application changes
    if (applicationRef.current !== application) {
      fetchedRef.current = false;
    }

    // Skip if we've already fetched for this application
    if (fetchedRef.current) return;

    // Mark as fetched to prevent multiple requests
    fetchedRef.current = true;

    if (previewMode && previewData) {
      // In preview mode, use the data passed directly
      setResumeId(previewData.resumeId || null);
      setTranscriptId(previewData.transcriptId || null);
      setSupplementalResponses(previewData.supplementalResponses || 'No supplemental responses provided.');
      return;
    }

    // Normal mode - fetch from API
    setAppLoading(true);
    setError(null);

    // Store the application ID to prevent closure issues
    const applicationId = application.applicationId;

    // Updated API call - removed labId parameter
    axiosPrivate.get(`/application/application?applicationId=${applicationId}`)
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
  }, [application?.applicationId, previewMode]); // Updated dependency array - removed labId

  // Memoize fetchPdf to prevent recreating on each render
  const fetchPdf = useCallback(async (fileId) => {
    if (!fileId) {
      setError('No file ID provided');
      return;
    }

    setLoading(true);
    setError(null);
    setPdfUrl(null);
    // Only reset useIframe if worker config didn't fail
    if (!pdfWorkerConfigFailed) {
      setUseIframe(false);
    }
    setNumPages(null);

    // Clear any existing timeout
    if (pdfTimeoutRef.current) {
      clearTimeout(pdfTimeoutRef.current);
      pdfTimeoutRef.current = null;
    }

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

      // Revoke the previous URL after setting the new one
      if (previousPdfUrlRef.current) {
        URL.revokeObjectURL(previousPdfUrlRef.current);
      }
      previousPdfUrlRef.current = url;

      // Only set timeout if we're not already using iframe
      if (!pdfWorkerConfigFailed) {
        // Set a timeout to fall back to iframe if react-pdf takes too long
        pdfTimeoutRef.current = setTimeout(() => {
          console.warn('PDF rendering timeout - falling back to iframe');
          setUseIframe(true);
        }, 5000); // 5 seconds timeout
      }
    } catch (error) {
      console.error('Error fetching PDF:', error);
      setError(error.response?.data?.message || error.message || 'Failed to load PDF. Please try again.');
    } finally {
      setLoading(false);
    }
  }, []);

  // Handle tab changes
  const handleTabChange = (event, newValue) => {
    // Clear error when switching tabs
    setError(null);
    setTabValue(newValue);
  };

  // Fetch PDF when tab changes
  useEffect(() => {
    if (tabValue === 0 && resumeId) {
      fetchPdf(resumeId);
    } else if (tabValue === 1 && transcriptId) {
      fetchPdf(transcriptId);
    } else if (tabValue < 2) {
      setPdfUrl(null);
    }

    return () => {
      // Clear any timeout
      if (pdfTimeoutRef.current) {
        clearTimeout(pdfTimeoutRef.current);
        pdfTimeoutRef.current = null;
      }
    };
  }, [tabValue, resumeId, transcriptId, fetchPdf]);

  // Clean up on unmount
  useEffect(() => {
    return () => {
      if (previousPdfUrlRef.current) {
        URL.revokeObjectURL(previousPdfUrlRef.current);
      }
    };
  }, []);

  const onDocumentLoadSuccess = ({ numPages }) => {
    // Clear the timeout since PDF loaded successfully
    if (pdfTimeoutRef.current) {
      clearTimeout(pdfTimeoutRef.current);
      pdfTimeoutRef.current = null;
    }

    setNumPages(numPages);
  };

  // Handle PDF loading error
  const onDocumentLoadError = (error) => {
    console.error('Error loading PDF document with react-pdf:', error);
    // Fall back to iframe
    setUseIframe(true);
  };

  // Handle any error in the PDF error boundary
  const handlePdfError = (error) => {
    console.error('PDF Error Boundary caught an error:', error);
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
          p: (tabValue === 0 || tabValue === 1) && pdfUrl ? (useIframe ? 0 : 3) : 3
        }}>
          {appLoading ? (
            <Box sx={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              height: '100%'
            }}>
              <CircularProgress size={40} />
            </Box>
          ) : error && tabValue !== 2 ? ( // Only show error if not on Supplemental Responses tab
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
        <Box sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100%'
        }}>
          <CircularProgress size={40} />
        </Box>
      );
    }

    if (error && !pdfUrl) {
      return (
        <Box>
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
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
        <Box sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100%'
        }}>
          <CircularProgress size={40} />
        </Box>
      );
    }

    // Use iframe if react-pdf failed, if worker config failed, or if we're forcing iframe
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

    // Try to render with react-pdf, wrapped in error handling
    try {
      return (
        <PDFErrorBoundary
          onError={handlePdfError}
          fallback={
            <Box sx={{ height: '100%', width: '100%' }}>
              <iframe
                src={pdfUrl}
                title={tabValue === 0 ? "Resume" : "Transcript"}
                width="100%"
                height="100%"
                style={{ border: 'none' }}
              />
            </Box>
          }
        >
          <Box
            sx={{
              p: 2,
              border: '1px solid #e0e0e0',
              borderRadius: '4px',
              backgroundColor: '#ffffff',
              overflowY: 'auto',
              height: '100%'
            }}
          >
            <Document
              file={pdfUrl}
              onLoadSuccess={onDocumentLoadSuccess}
              onLoadError={onDocumentLoadError}
              loading={
                <Box sx={{
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  height: '100%',
                  minHeight: 300
                }}>
                  <CircularProgress size={40} />
                </Box>
              }
              error={
                <Box sx={{
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  height: '100%'
                }}>
                  <CircularProgress size={40} />
                </Box>
              }
            >
              {numPages && Array.from(new Array(numPages), (el, index) => (
                <Box key={`page_${index + 1}`} sx={{
                  mb: 3,
                  display: 'flex',
                  justifyContent: 'center'
                }}>
                  <Page
                    pageNumber={index + 1}
                    renderTextLayer={true}
                    renderAnnotationLayer={true}
                    width={Math.min(600, window.innerWidth - 250)} // Responsive width
                    onRenderError={(error) => {
                      console.error(`Error rendering page ${index + 1}:`, error);
                      if (index === 0) setUseIframe(true);
                    }}
                    error={
                      <Box sx={{ textAlign: 'center', color: 'text.secondary', py: 2 }}>
                        Error loading page {index + 1}
                      </Box>
                    }
                  />
                </Box>
              ))}
            </Document>
          </Box>
        </PDFErrorBoundary>
      );
    } catch (error) {
      console.error('Unexpected error rendering PDF with react-pdf:', error);
      // If any unexpected error occurs during rendering, fall back to iframe
      setUseIframe(true);
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
  }
};

export default ApplicationViewer
