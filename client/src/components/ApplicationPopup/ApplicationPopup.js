import React, { useState, useEffect } from 'react';
import { axiosPrivate } from '../../apis/backend';
import { Box, Button, Typography, Modal, Paper, Stepper, Step, StepLabel } from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

const steps = ['Upload Documents', 'Supplemental Questions', 'Review & Submit'];

const ApplicationPopup = ({ open, onClose, questions, postingId }) => {
    const [resumeMetadata, setResumeMetadata] = useState(null);
    const [transcriptMetadata, setTranscriptMetadata] = useState(null);
    const [answers, setAnswers] = useState(questions);
    const [activeStep, setActiveStep] = useState(0);

    const fetchFileMetadata = async () => {
        try {
            const resumeResponse = await axiosPrivate.get('/applicant/resumeMetadata');
            if (resumeResponse.data.payload) setResumeMetadata(resumeResponse.data.payload.resumeMetadata);
        } catch (error) {
            setResumeMetadata(null);
        }

        try {
            const transcriptResponse = await axiosPrivate.get('/applicant/transcriptMetadata');
            if (transcriptResponse.data.payload) setTranscriptMetadata(transcriptResponse.data.payload.transcriptMetadata);
        } catch (error) {
            setTranscriptMetadata(null);
        }
    };

    useEffect(() => {
        fetchFileMetadata();
    }, []);

    const handleFileUpload = async (event, type) => {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        if (type === 'resume') {
            formData.append('resume', file);
        }
        if (type === 'transcript') {
            formData.append('transcript', file);
        }

        const config = {
            headers: { 'Content-Type': 'multipart/form-data' }
        };
        try {
            if (type === 'resume') {
                await axiosPrivate.post('/applicant/resume', formData, config);
            } else {
                await axiosPrivate.post('/applicant/transcript', formData, config);
            }
            fetchFileMetadata(); // Refetch file metadata after upload
        } catch (error) {
            console.error('File upload error:', error);
        }
    };

    const handleNext = () => {
        setActiveStep((prevStep) => prevStep + 1);
    };

    const handleBack = () => {
        setActiveStep((prevStep) => prevStep - 1);
    };

    const handleSubmit = async () => {
        const body = {
            application: {
                resumeId: resumeMetadata ? resumeMetadata.fileId : null,
                transcriptId: transcriptMetadata ? transcriptMetadata.fileId : null,
                supplementalResponses: answers
            }
        };
        try {
            await axiosPrivate.post(`/application?positionId=${postingId}`, body);
            onClose(); // Close the modal after submission
        } catch (error) {
            console.error('Submission error:', error);
        }
    }

    if (!open) return null; // Prevent rendering if modal is closed
    if (activeStep === 3) return null; // Prevent rendering if all steps are completed

    return (
        <Modal open={open} onClose={onClose}>
            <Box sx={{
                position: 'absolute',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                width: '70%',
                bgcolor: 'background.paper',
                boxShadow: 24,
                p: 4,
                borderRadius: 2,
                overflowY: 'auto',
                height: '80vh',
                maxHeight: '80vh'
            }}>
                <Stepper activeStep={activeStep} sx={{ mb: 3 }}>
                    {steps.map((label) => (
                        <Step key={label}>
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    ))}
                </Stepper>
                {activeStep === 0 && (
                    <Paper sx={{ p: 3, minHeight: 300, display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <Typography variant="h6">Upload Documents</Typography>
                        <Box mt={2}>
                            <Typography variant="h6">Resume</Typography>
                            {resumeMetadata && <>
                                <Typography color='blue'>File: {resumeMetadata.fileName}</Typography>
                                {/* Convert Unix timestamp to human-readable format */}
                                <Typography>Last updated: {(new Date(resumeMetadata.uploadedTimeStamp)).toLocaleDateString()}</Typography>
                            </>
                            }
                            <Button variant="outlined" component="label" startIcon={<CloudUploadIcon />}>
                                Upload Resume
                                <input type="file" hidden onChange={(e) => handleFileUpload(e, 'resume')} />
                            </Button>
                        </Box>
                        <Box mt={2}>
                            <Typography variant="h6">Unofficial Transcript</Typography>
                            {transcriptMetadata && <>
                                <Typography color='blue'>File: {transcriptMetadata.fileName}</Typography>
                                {/* Convert Unix timestamp to human-readable format */}
                                <Typography>Last updated: {(new Date(transcriptMetadata.uploadedTimeStamp)).toLocaleDateString()}</Typography>
                            </>
                            }
                            <Button variant="outlined" component="label" startIcon={<CloudUploadIcon />}>
                                Upload Transcript
                                <input type="file" hidden onChange={(e) => handleFileUpload(e, 'transcript')} />
                            </Button>
                        </Box>
                        <Box display="flex" justifyContent="end" sx={{ marginTop: 'auto' }}>
                            <Button onClick={handleNext} variant="contained" sx={{ mt: 3 }}>Next</Button>
                        </Box>
                    </Paper>
                )}
                {activeStep === 1 && (
                    <Paper sx={{ p: 3, minHeight: 300, display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <Typography variant="body1">Answer the following questions:</Typography>
                        <Box sx={{
                            '.ql-editor': {
                                overflow: 'hidden',
                                resize: 'vertical',
                                minHeight: '80px'
                            }
                        }}>
                            <ReactQuill value={answers} onChange={setAnswers} />
                        </Box>
                        {/* <button onClick={() => console.log(answers)}>Log answers</button> */}
                        <Box display="flex" justifyContent="space-between" sx={{ marginTop: 'auto' }}>
                            <Button onClick={handleBack}>Back</Button>
                            <Button onClick={handleNext} variant="contained">Next</Button>
                        </Box>
                    </Paper>
                )}
                {activeStep === 2 && (
                    <Paper sx={{ p: 3, minHeight: 300, display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <Typography variant="h6">Review & Submit</Typography>
                        <Typography variant="body1" color='blue'>Resume: {resumeMetadata.fileName || 'Not uploaded'}</Typography>
                        <Typography variant="body1" color='blue'>Transcript: {transcriptMetadata.fileName || 'Not uploaded'}</Typography>
                        <Typography variant="h6">Supplemental Responses:</Typography>
                        <ReactQuill
                            value={answers}
                            readOnly={true} // Makes the editor non-editable
                            theme="snow"  // Use the Snow theme or any other theme
                            modules={{
                                toolbar: false // This removes the toolbar
                            }}
                        />
                        <Box display="flex" justifyContent="space-between" sx={{ marginTop: 'auto' }}>
                            <Button onClick={handleBack}>Back</Button>
                            <Button variant="contained" color="primary" onClick={handleSubmit}>Submit</Button>
                        </Box>
                    </Paper>
                )}
            </Box>
        </Modal>
    );
};

export default ApplicationPopup;
