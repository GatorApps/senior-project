import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  Box,
  Typography,
  Tabs,
  Tab,
  CircularProgress,
  Alert,
  Divider,
  FormControl,
  Select,
  MenuItem,
  IconButton,
  Tooltip
} from '@mui/material';
import { Download as DownloadIcon, Close as CloseIcon } from '@mui/icons-material';
import { axiosPrivate } from '../../apis/backend';
import { Document, Page, pdfjs } from 'react-pdf';
import 'react-pdf/dist/esm/Page/TextLayer.css';
import 'react-pdf/dist/esm/Page/AnnotationLayer.css';

// IMPORTANT: Fix for PDF.js worker
pdfjs.GlobalWorkerOptions.workerSrc = `https://unpkg.com/pdfjs-dist@2.16.105/build/pdf.worker.min.js`;

const ApplicationViewer = ({ open, onClose, application }) => {
  const [tabValue, setTabValue] = useState(0);
  const [numPages, setNumPages] = useState(null);
  const [pdfUrl, setPdfUrl] = useState(null);
  const [loading, setLoading] = useState(false);
  const [appLoading, setAppLoading] = useState(false);
  const [error, setError] = useState(null);
  const [supplementalResponses, setSupplementalResponses] = useState('');
  const [resumeId, setResumeId] = useState(null);
  const [transcriptId, setTranscriptId] = useState(null);
  const [status, setStatus] = useState('submitted');
  const [statusUpdating, setStatusUpdating] = useState(false);
  const [applicantInfo, setApplicantInfo] = useState({
    firstName: '',
    lastName: '',
    email: ''
  });

  // Fetch application details when application prop changes
  useEffect(() => {
    if (!application) return;

    setTabValue(0);
    setAppLoading(true);
    setError(null);

    axiosPrivate.get(`/application/application?labId=${application.labId}&applicationId=${application.applicationId}`)
      .then((response) => {
        if (response.data && response.data.errCode === '0' && response.data.payload.application) {
          const app = response.data.payload.application;
          setSupplementalResponses(app.supplementalResponses || 'No supplemental responses provided.');
          setResumeId(app.resumeId);
          setTranscriptId(app.transcriptId);
          setStatus(app.status || 'submitted');

          // Set applicant info from the new response format
          setApplicantInfo({
            firstName: app.firstName || '',
            lastName: app.lastName || '',
            email: app.email || ''
          });
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

  // Update application status
  const updateApplicationStatus = async (newStatus) => {
    if (!application) return;

    setStatusUpdating(true);
    try {
      await axiosPrivate.put(`/application/applicationStatus`, null, {
        params: {
          labId: application.labId,
          applicationId: application.applicationId,
          status: newStatus
        }
      });

      setStatus(newStatus);
    } catch (error) {
      console.error('Error updating status:', error);
      setError(error.response?.data?.message || error.message || 'Failed to update application status');
    } finally {
      setStatusUpdating(false);
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
    console.error('Error loading PDF document:', error);
    setError('Failed to load the PDF document. Try downloading it instead.');
  };

  // Generate filename for download
  const getDownloadFilename = () => {
    const { firstName, lastName } = applicantInfo;
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
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
      PaperProps={{
        sx: {
          height: '80vh',
          display: 'flex',
          flexDirection: 'column'
        }
      }}
    >
      <DialogTitle sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center', // Changed to center for vertical alignment
        pb: 2,
        pr: 1
      }}>
        <Box>
          {applicantInfo.firstName && (
            <Typography variant="subtitle1">
              {`${applicantInfo.firstName} ${applicantInfo.lastName}`}
            </Typography>
          )}
          {applicantInfo.email && (
            <Typography variant="body2" color="text.secondary">
              {applicantInfo.email}
            </Typography>
          )}
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <FormControl sx={{ width: 180 }}>
            <Select
              value={status}
              onChange={(e) => updateApplicationStatus(e.target.value)}
              size="small"
              disabled={statusUpdating}
              sx={{
                height: 36,
                '& .MuiSelect-select': {
                  display: 'flex',
                  alignItems: 'center',
                  paddingRight: statusUpdating ? '32px' : '32px' // Keep consistent padding
                }
              }}
              MenuProps={{
                PaperProps: {
                  style: { width: 180 }
                }
              }}
              // Render value with loading indicator inside
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', alignItems: 'center', position: 'relative' }}>
                  {selected === 'submitted' ? 'Active' :
                    selected === 'moving forward' ? 'Moving Forward' : 'Archived'}
                  {statusUpdating && (
                    <CircularProgress
                      size={16}
                      sx={{
                        position: 'absolute',
                        right: -20,
                        color: 'primary.main'
                      }}
                    />
                  )}
                </Box>
              )}
            >
              <MenuItem value="submitted">Active</MenuItem>
              <MenuItem value="moving forward">Moving Forward</MenuItem>
              <MenuItem value="archived">Archived</MenuItem>
            </Select>
          </FormControl>

          <IconButton
            onClick={onClose}
            size="small"
            aria-label="close"
            sx={{
              ml: 1,
              '&:hover': {
                backgroundColor: 'rgba(0, 0, 0, 0.04)'
              }
            }}
          >
            <CloseIcon />
          </IconButton>
        </Box>
      </DialogTitle>

      <Divider />

      <DialogContent
        sx={{
          flex: 1,
          display: 'flex',
          p: 0,
          overflow: 'hidden'
        }}
      >
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
                position: 'relative' // For positioning the download button
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
            <Tab label="Supplemental Questions" />
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
            p: (tabValue === 0 || tabValue === 1) && pdfUrl ? 0 : 3
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
      </DialogContent>
    </Dialog>
  );

  // Helper function to render PDF content
  function renderPdfContent() {
    if (loading) {
      return (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
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

    // Use iframe for PDF viewing - most reliable cross-browser solution
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
};

export default ApplicationViewer;