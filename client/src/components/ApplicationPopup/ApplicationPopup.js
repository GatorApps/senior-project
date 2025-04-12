import React, { useState, useEffect } from 'react';
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
  Tooltip
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import DownloadIcon from '@mui/icons-material/Download';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { axiosPrivate } from '../../apis/backend';
import ApplicationViewer from '../ApplicationViewer/ApplicationViewer';

const steps = ['Upload Documents', 'Supplemental Questions', 'Review & Submit'];

const ApplicationPopup = ({ open, onClose, questions, postingId }) => {
  const [resumeMetadata, setResumeMetadata] = useState(null);
  const [transcriptMetadata, setTranscriptMetadata] = useState(null);
  const [answers, setAnswers] = useState('');
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [hasSupplementalQuestions, setHasSupplementalQuestions] = useState(true);

  // Reset state when modal opens
  useEffect(() => {
    if (open) {
      setError(null);
      setSuccess(false);
      setActiveStep(0); // Reset stepper when modal opens

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
        setError('Please upload your resume before proceeding.');
        return;
      }
      // Check if transcript is uploaded (now required)
      if (!transcriptMetadata) {
        setError('Please upload your transcript before proceeding.');
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
      setTimeout(() => {
        onClose(); // Close the modal after showing success message
      }, 2000);
    } catch (error) {
      console.error('Submission error:', error);
      setError(error.response?.data?.message || 'Failed to submit application. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    // Confirm before closing if user has started the application
    if (activeStep > 0 && !success) {
      if (window.confirm('Are you sure you want to close? Your progress will not be saved.')) {
        onClose();
      }
    } else {
      onClose();
    }
  };

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
    <Modal
      open={open}
      onClose={handleClose}
      aria-labelledby="application-modal-title"
    >
      <Box sx={{
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
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
        {success ? (
          <Box sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100%'
          }}>
            <Alert severity="success" sx={{ mb: 2 }}>
              Application submitted successfully!
            </Alert>
            <Typography variant="body1">
              Thank you for your application. You will be redirected shortly.
            </Typography>
          </Box>
        ) : (
          <>
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
                        labId: postingId,
                        applicationId: 'preview'
                      }}
                      // applicantInfo={{
                      //   firstName: 'Preview',
                      //   lastName: 'User'
                      // }}
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
                onClick={handleClose}
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
          </>
        )}
      </Box>
    </Modal>
  );
};

export default ApplicationPopup
