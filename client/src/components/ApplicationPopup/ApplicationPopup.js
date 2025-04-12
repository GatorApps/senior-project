import React, { useState, useEffect, useCallback, useRef } from 'react';
import {
  Box,
  Button,
  Typography,
  Modal,
  Stepper,
  Step,
  StepLabel,
  Alert,
  CircularProgress,
  Divider,
  IconButton,
  Tooltip,
  Paper,
  Grow,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import DownloadIcon from '@mui/icons-material/Download';
import CelebrationIcon from '@mui/icons-material/Celebration';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import WarningIcon from '@mui/icons-material/Warning';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { axiosPrivate } from '../../apis/backend';
import ApplicationViewer from '../ApplicationViewer/ApplicationViewer';
import Confetti from 'react-confetti';
import { useNavigate } from 'react-router-dom';

const steps = ['Upload Documents', 'Supplemental Questions', 'Review & Submit'];

// Custom Grow wrapper component that maintains center positioning
const CenteredGrow = ({ children, ...props }) => (
  <Grow {...props}>
    <div style={{
      position: 'absolute',
      top: '50%',
      left: '50%',
      transform: 'translate(-50%, -50%)',
      transformOrigin: 'center',
      width: '100%',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center'
    }}>
      {children}
    </div>
  </Grow>
);

const ApplicationPopup = ({ open, onClose, questions, postingId }) => {
  const [resumeMetadata, setResumeMetadata] = useState(null);
  const [transcriptMetadata, setTranscriptMetadata] = useState(null);
  const [answers, setAnswers] = useState('');
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [hasSupplementalQuestions, setHasSupplementalQuestions] = useState(true);
  const [showConfetti, setShowConfetti] = useState(false);
  const [confettiOpacity, setConfettiOpacity] = useState(1);
  const [windowSize, setWindowSize] = useState({
    width: window.innerWidth,
    height: window.innerHeight
  });
  const [isCelebrateDisabled, setIsCelebrateDisabled] = useState(false);
  const [isClosing, setIsClosing] = useState(false);
  const [showConfirmDialog, setShowConfirmDialog] = useState(false);
  const confettiFadeTimeoutRef = useRef(null);
  const fadeIntervalRef = useRef(null);
  const navigate = useNavigate();

  // Update window size for confetti
  useEffect(() => {
    const handleResize = () => {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight
      });
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Reset state when modal opens
  useEffect(() => {
    if (open) {
      setError(null);
      setSuccess(false);
      setShowConfetti(false);
      setConfettiOpacity(1);
      setActiveStep(0); // Reset stepper when modal opens
      setIsCelebrateDisabled(false);
      setIsClosing(false);
      setShowConfirmDialog(false);

      // Check if position has supplemental questions
      if (questions === null || questions === undefined || questions === '') {
        setHasSupplementalQuestions(false);
        setAnswers('');
      } else {
        setHasSupplementalQuestions(true);
        setAnswers(questions);
      }
    }
  }, [open, questions]);

  // Cleanup confetti fade timeout on unmount
  useEffect(() => {
    return () => {
      if (confettiFadeTimeoutRef.current) {
        clearTimeout(confettiFadeTimeoutRef.current);
      }
      if (fadeIntervalRef.current) {
        clearInterval(fadeIntervalRef.current);
      }
    };
  }, []);

  // Add beforeunload event listener for browser navigation
  useEffect(() => {
    const handleBeforeUnload = (e) => {
      if (open && activeStep > 0 && !success) {
        e.preventDefault();
        e.returnValue = ''; // This is required for Chrome
        return ''; // This is required for other browsers
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [open, activeStep, success]);

  const fetchFileMetadata = async () => {
    setLoading(true);
    setError(null);

    try {
      const resumeResponse = await axiosPrivate.get('/applicant/resumeMetadata');
      if (resumeResponse.data.payload) {
        setResumeMetadata(resumeResponse.data.payload.resumeMetadata);
      }
    } catch (error) {
      console.error('Error fetching resume metadata:', error);
      setResumeMetadata(null);
    }

    try {
      const transcriptResponse = await axiosPrivate.get('/applicant/transcriptMetadata');
      if (transcriptResponse.data.payload) {
        setTranscriptMetadata(transcriptResponse.data.payload.transcriptMetadata);
      }
    } catch (error) {
      console.error('Error fetching transcript metadata:', error);
      setTranscriptMetadata(null);
    }

    setLoading(false);
  };

  useEffect(() => {
    if (open) {
      fetchFileMetadata();
    }
  }, [open]);

  const handleFileUpload = async (event, type) => {
    const file = event.target.files[0];
    if (!file) return;

    // Validate file type (PDF only)
    if (file.type !== 'application/pdf') {
      setError(`${type === 'resume' ? 'Resume' : 'Transcript'} must be a PDF file.`);
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      setError(`${type === 'resume' ? 'Resume' : 'Transcript'} must be less than 5MB.`);
      return;
    }

    setLoading(true);
    setError(null);

    const formData = new FormData();
    formData.append(type, file);

    const config = {
      headers: { 'Content-Type': 'multipart/form-data' }
    };

    try {
      await axiosPrivate.post(`/applicant/${type}`, formData, config);
      await fetchFileMetadata(); // Refetch file metadata after upload
      setError(null);
    } catch (error) {
      console.error('File upload error:', error);
      setError(error.response?.data?.message || `Failed to upload ${type}. Please try again.`);
    } finally {
      setLoading(false);
    }
  };

  const handleNext = () => {
    // Validate current step before proceeding
    if (activeStep === 0) {
      // Check if resume is uploaded
      if (!resumeMetadata) {
        setError('Please upload your resume');
        return;
      }
      // Check if transcript is uploaded (now required)
      if (!transcriptMetadata) {
        setError('Please upload your transcript');
        return;
      }
    } else if (activeStep === 1 && hasSupplementalQuestions) {
      // Only validate answers if there are supplemental questions
      const cleanAnswers = answers || '';
      const isEmptyHtml = cleanAnswers === '<p><br></p>' || cleanAnswers === '';

      if (isEmptyHtml) {
        setError('Please answer the supplemental questions before proceeding.');
        return;
      }
    }

    setError(null);
    setActiveStep((prevStep) => prevStep + 1);
  };

  const handleBack = () => {
    setError(null);
    setActiveStep((prevStep) => prevStep - 1);
  };

  const handleSubmit = async () => {
    setLoading(true);
    setError(null);

    const body = {
      application: {
        resumeId: resumeMetadata ? resumeMetadata.fileId : null,
        transcriptId: transcriptMetadata ? transcriptMetadata.fileId : null,
        supplementalResponses: hasSupplementalQuestions ? answers : null
      }
    };

    try {
      await axiosPrivate.post(`/application?positionId=${postingId}`, body);
      setSuccess(true);
      startConfetti();
      // No longer automatically closing the modal
    } catch (error) {
      console.error('Submission error:', error);
      setError(error.response?.data?.message || 'Failed to submit application. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleCloseRequest = () => {
    // If already in closing state, do nothing
    if (isClosing) return;

    // Show confirmation dialog if user has started the application and not in success state
    if (activeStep > 0 && !success) {
      setShowConfirmDialog(true);
    } else {
      // Start close animation
      closeWithAnimation();
    }
  };

  const closeWithAnimation = () => {
    setIsClosing(true);
    setTimeout(() => {
      onClose();
    }, 300); // Match the duration with the Grow timeout
  };

  const handleConfirmClose = () => {
    setShowConfirmDialog(false);
    closeWithAnimation();
  };

  const handleCancelClose = () => {
    setShowConfirmDialog(false);
  };

  // Start confetti with fade out effect
  const startConfetti = useCallback(() => {
    // Disable the celebrate button while animation is running
    setIsCelebrateDisabled(true);

    // Clear any existing timeout and interval
    if (confettiFadeTimeoutRef.current) {
      clearTimeout(confettiFadeTimeoutRef.current);
    }

    if (fadeIntervalRef.current) {
      clearInterval(fadeIntervalRef.current);
    }

    // Reset and show confetti
    setShowConfetti(false); // First hide to reset the animation
    setTimeout(() => {
      setShowConfetti(true);
      setConfettiOpacity(1);

      // Start fade out after 4 seconds
      confettiFadeTimeoutRef.current = setTimeout(() => {
        // Gradually reduce opacity over 1 second
        fadeIntervalRef.current = setInterval(() => {
          setConfettiOpacity((prevOpacity) => {
            const newOpacity = prevOpacity - 0.05;
            if (newOpacity <= 0) {
              clearInterval(fadeIntervalRef.current);
              setShowConfetti(false);
              setIsCelebrateDisabled(false); // Re-enable the celebrate button
              return 0;
            }
            return newOpacity;
          });
        }, 50);
      }, 4000);
    }, 50); // Small delay to ensure the component has time to unmount
  }, []);

  const handleCelebrate = useCallback(() => {
    if (!isCelebrateDisabled) {
      startConfetti();
    }
  }, [startConfetti, isCelebrateDisabled]);

  const handleTrackApplications = useCallback(() => {
    setIsClosing(true);
    setTimeout(() => {
      navigate('/myapplications');
      onClose();
    }, 300); // Match the duration with the Grow timeout
  }, [navigate, onClose]);

  const handleDownload = (fileId, fileName) => {
    axiosPrivate.get(`/file/${fileId}`, {
      responseType: 'blob',
    })
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        link.remove();
      })
      .catch(error => {
        console.error('Download error:', error);
        setError('Failed to download file. Please try again.');
      });
  };

  if (!open) return null;

  return (
    <>
      {/* Custom Confirmation Dialog */}
      <Dialog
        open={showConfirmDialog}
        onClose={handleCancelClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <WarningIcon color="warning" />
          {"Unsaved Changes"}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure you want to close? Your progress will not be saved.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancelClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleConfirmClose} color="error" autoFocus>
            Close Without Saving
          </Button>
        </DialogActions>
      </Dialog>

      {/* Main Modal */}
      <Modal
        open={open}
        onClose={success ? null : handleCloseRequest} // Disable onClose when in success state
        aria-labelledby="application-modal-title"
      >
        <CenteredGrow in={!isClosing} timeout={300}>
          {success ? (
            // Smaller confirmation window
            <Paper sx={{
              width: '90%',
              maxWidth: '600px',
              display: 'flex',
              flexDirection: 'column',
              overflow: 'hidden',
              borderRadius: 2,
              boxShadow: 24,
            }}>
              {/* Confetti overlay with opacity transition and pointer-events: none */}
              {showConfetti && (
                <Box sx={{
                  position: 'fixed',
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  zIndex: 9999,
                  opacity: confettiOpacity,
                  transition: 'opacity 1s ease-out',
                  pointerEvents: 'none' // This allows clicks to pass through to elements underneath
                }}>
                  <Confetti
                    width={windowSize.width}
                    height={windowSize.height}
                    recycle={false}
                    numberOfPieces={500}
                    gravity={0.2}
                    initialVelocityY={10}
                    tweenDuration={5000}
                  />
                </Box>
              )}

              {/* Green header with checkmark */}
              <Box sx={{
                bgcolor: '#4CAF50',
                color: 'white',
                p: 4,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                position: 'relative',
                overflow: 'hidden'
              }}>
                <Box sx={{
                  borderRadius: '50%',
                  p: 1,
                  mb: 2,
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  width: 80,
                  height: 80
                }}>
                  <CheckCircleIcon sx={{ fontSize: 80, color: 'white' }} />
                </Box>
                <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
                  Submitted
                </Typography>
              </Box>

              {/* Content area */}
              <Box sx={{ p: 4, bgcolor: 'white' }}>
                <Typography variant="h6" color="primary" sx={{ mb: 2, fontWeight: 'bold' }}>
                  Way to go, your application is in!
                </Typography>

                <Typography variant="body1" sx={{ mb: 2 }}>
                  You have successfully submitted your application. The lab will review your application and contact you if they're interested.
                </Typography>

                <Typography variant="body1" sx={{ mb: 2 }}>
                  Should you have any questions regarding the position or your application, please reach out to the lab directly. For technical support regarding the RESEARCH.UF platform, please contact support@gatorapps.org.
                </Typography>

                <Typography variant="body1" sx={{ mb: 3 }}>
                  What's next? You can track the status of your application and view all your submitted applications in the My Applications module.
                </Typography>
              </Box>

              {/* Buttons at the bottom */}
              <Box sx={{
                p: 3,
                display: 'flex',
                justifyContent: 'space-between',
                borderTop: '1px solid #e0e0e0',
                bgcolor: '#f5f5f5',
                position: 'relative', // Ensure buttons are above confetti
                zIndex: 1 // Not necessary with pointerEvents: none on confetti, but added for extra safety
              }}>
                <Button
                  variant="outlined"
                  onClick={handleCloseRequest}
                  size="small"
                  sx={{
                    minWidth: '100px',
                    height: '36px',
                    whiteSpace: 'nowrap'
                  }}
                >
                  Close
                </Button>

                <Box sx={{ display: 'flex', gap: 2 }}>
                  <Button
                    variant="outlined"
                    onClick={handleCelebrate}
                    disabled={isCelebrateDisabled}
                    size="small"
                    startIcon={isCelebrateDisabled ?
                      <CircularProgress size={16} color="inherit" /> :
                      <CelebrationIcon />
                    }
                    sx={{
                      minWidth: '120px',
                      height: '36px',
                      whiteSpace: 'nowrap'
                    }}
                  >
                    Celebrate
                  </Button>

                  <Button
                    variant="contained"
                    color="primary"
                    onClick={handleTrackApplications}
                    size="small"
                    sx={{
                      minWidth: '160px',
                      height: '36px',
                      whiteSpace: 'nowrap'
                    }}
                  >
                    Track My Applications
                  </Button>
                </Box>
              </Box>
            </Paper>
          ) : (
            // Regular application form window
            <Box sx={{
              width: '80%',
              maxWidth: '1000px',
              bgcolor: 'background.paper',
              boxShadow: 24,
              p: 4,
              borderRadius: 2,
              overflowY: 'auto',
              height: '80vh',
              maxHeight: '80vh',
              display: 'flex',
              flexDirection: 'column'
            }}>
              <Typography id="application-modal-title" variant="h5" component="h2" sx={{ mb: 3 }}>
                Submit Application
              </Typography>

              <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
                {steps.map((label) => (
                  <Step key={label}>
                    <StepLabel>{label}</StepLabel>
                  </Step>
                ))}
              </Stepper>

              {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                  {error}
                </Alert>
              )}

              {loading && (
                <Box sx={{
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  backgroundColor: 'rgba(255, 255, 255, 0.7)',
                  zIndex: 1
                }}>
                  <CircularProgress />
                </Box>
              )}

              <Box sx={{ flex: 1, overflowY: 'auto', mb: 2 }}>
                {activeStep === 0 && (
                  <Box sx={{ p: 3, minHeight: 300, display: 'flex', flexDirection: 'column', gap: 3 }}>
                    <Box>
                      <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                        Resume *
                      </Typography>
                      {resumeMetadata && (
                        <Box sx={{ mb: 2, p: 2, border: '1px solid #e0e0e0', borderRadius: 1, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                          <Box>
                            <Typography color="primary">
                              {resumeMetadata.fileName}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              Last updated: {new Date(resumeMetadata.uploadedTimeStamp).toLocaleString()}
                            </Typography>
                          </Box>
                          <Box>
                            <Tooltip title="Download Resume">
                              <IconButton
                                onClick={() => handleDownload(resumeMetadata.fileId, resumeMetadata.fileName)}
                                size="small"
                              >
                                <DownloadIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>
                          </Box>
                        </Box>
                      )}
                      <Button
                        variant="outlined"
                        component="label"
                        startIcon={<CloudUploadIcon />}
                        disabled={loading}
                      >
                        {resumeMetadata ? 'Replace Resume' : 'Upload Resume'}
                        <input
                          type="file"
                          hidden
                          accept=".pdf"
                          onChange={(e) => handleFileUpload(e, 'resume')}
                        />
                      </Button>
                      <Typography variant="caption" display="block" sx={{ mt: 1 }}>
                        PDF format only, max 5MB
                      </Typography>
                    </Box>

                    <Divider />

                    <Box>
                      <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                        Transcript *
                      </Typography>
                      {transcriptMetadata && (
                        <Box sx={{ mb: 2, p: 2, border: '1px solid #e0e0e0', borderRadius: 1, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                          <Box>
                            <Typography color="primary">
                              {transcriptMetadata.fileName}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              Last updated: {new Date(transcriptMetadata.uploadedTimeStamp).toLocaleString()}
                            </Typography>
                          </Box>
                          <Box>
                            <Tooltip title="Download Transcript">
                              <IconButton
                                onClick={() => handleDownload(transcriptMetadata.fileId, transcriptMetadata.fileName)}
                                size="small"
                              >
                                <DownloadIcon fontSize="small" />
                              </IconButton>
                            </Tooltip>
                          </Box>
                        </Box>
                      )}
                      <Button
                        variant="outlined"
                        component="label"
                        startIcon={<CloudUploadIcon />}
                        disabled={loading}
                      >
                        {transcriptMetadata ? 'Replace Transcript' : 'Upload Transcript'}
                        <input
                          type="file"
                          hidden
                          accept=".pdf"
                          onChange={(e) => handleFileUpload(e, 'transcript')}
                        />
                      </Button>
                      <Typography variant="caption" display="block" sx={{ mt: 1 }}>
                        Unofficial is fine, PDF format only, max 5MB
                      </Typography>
                    </Box>
                  </Box>
                )}

                {activeStep === 1 && (
                  <Box sx={{ p: 3, minHeight: 300, display: 'flex', flexDirection: 'column', gap: 3 }}>
                    {!hasSupplementalQuestions ? (
                      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
                        <Alert severity="info" sx={{ width: '100%' }}>
                          This position does not have supplemental questions. Please click NEXT to proceed
                        </Alert>
                      </Box>
                    ) : (
                      <>
                        <Typography variant="body1" gutterBottom>
                          Please answer the following questions:
                        </Typography>

                        <Box sx={{
                          '.ql-editor': {
                            minHeight: '200px',
                            maxHeight: '400px',
                            overflow: 'auto'
                          },
                          '.ql-container': {
                            borderBottomLeftRadius: '4px',
                            borderBottomRightRadius: '4px'
                          },
                          '.ql-toolbar': {
                            borderTopLeftRadius: '4px',
                            borderTopRightRadius: '4px'
                          }
                        }}>
                          <ReactQuill
                            value={answers}
                            onChange={setAnswers}
                            placeholder="Type your answers here..."
                          />
                        </Box>
                      </>
                    )}
                  </Box>
                )}

                {activeStep === 2 && (
                  <Box sx={{
                    flex: 1,
                    display: 'flex',
                    flexDirection: 'column',
                    height: 'calc(100% - 20px)'
                  }}>
                    <Box sx={{
                      flex: 1,
                      border: '1px solid #e0e0e0',
                      borderRadius: 1,
                      overflow: 'hidden'
                    }}>
                      <ApplicationViewer
                        application={{
                          applicationId: 'preview'
                        }}
                        applicantInfo={{
                          firstName: 'Preview',
                          lastName: 'User'
                        }}
                        previewMode={true}
                        previewData={{
                          resumeId: resumeMetadata?.fileId,
                          transcriptId: transcriptMetadata?.fileId,
                          supplementalResponses: hasSupplementalQuestions ? answers : 'This position does not have supplemental questions'
                        }}
                      />
                    </Box>
                  </Box>
                )}
              </Box>

              <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
                mt: 'auto',
                pt: 2,
                borderTop: '1px solid #e0e0e0'
              }}>
                <Button
                  onClick={handleCloseRequest}
                  color="error"
                  variant="outlined"
                >
                  Cancel
                </Button>

                <Box>
                  {activeStep > 0 && (
                    <Button
                      onClick={handleBack}
                      sx={{ mr: 1 }}
                      disabled={loading}
                    >
                      Back
                    </Button>
                  )}

                  {activeStep < steps.length - 1 ? (
                    <Button
                      onClick={handleNext}
                      variant="contained"
                      disabled={loading}
                    >
                      Next
                    </Button>
                  ) : (
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={handleSubmit}
                      disabled={loading}
                    >
                      Submit Application
                    </Button>
                  )}
                </Box>
              </Box>
            </Box>
          )}
        </CenteredGrow>
      </Modal>
    </>
  );
};

export default ApplicationPopup
