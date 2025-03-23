import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Box,
    Typography,
    Tabs,
    Tab,
} from '@mui/material';
import { axiosPrivate } from '../../apis/backend';
import { Document, Page, pdfjs } from 'react-pdf';
import 'react-pdf/dist/esm/Page/TextLayer.css';
// import 'react-pdf/dist/esm/Page/AnnotationLayer.css'; // Optional: For annotations

pdfjs.GlobalWorkerOptions.workerSrc = new URL(
    'pdfjs-dist/build/pdf.worker.mjs',
    import.meta.url
).toString();

const ApplicationViewer = ({ open, onClose, application }) => {
    const [tabValue, setTabValue] = useState(0);
    const [numPages, setNumPages] = useState(null);
    const [pdfUrl, setPdfUrl] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const { supplementalResponses, resumeId, transcriptId } = application || {};

    const fetchPdf = async (fileId) => {
        setLoading(true);
        setError(null);

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
            setError('Failed to load PDF. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (tabValue === 1 && resumeId) {
            fetchPdf(resumeId);
        } else if (tabValue === 2 && transcriptId) {
            fetchPdf(transcriptId);
        } else {
            setPdfUrl(null);
        }

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

    if (!application) return null;

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>Application Details</DialogTitle>
            <DialogContent dividers sx={{ height: '600px', overflow: 'hidden' }}>
                <Tabs value={tabValue} onChange={handleTabChange}>
                    <Tab label="Supplemental Questions" />
                    <Tab label="Resume" />
                    <Tab label="Transcript" />
                </Tabs>

                <Box sx={{ height: '520px', overflowY: 'auto', paddingTop: 2 }}>
                    {tabValue === 0 && (
                        <Box>
                            <Typography variant="h6">Supplemental Responses</Typography>
                            <Box
                                sx={{
                                    padding: 2,
                                    border: '1px solid #ddd',
                                    borderRadius: '4px',
                                    backgroundColor: '#f9f9f9',
                                    marginTop: 2,
                                }}
                                dangerouslySetInnerHTML={{ __html: supplementalResponses }}
                            />
                        </Box>
                    )}

                    {tabValue > 0 && (
                        <Box sx={{ height: '100%', overflowY: 'auto' }}>
                            {loading && <Typography>Loading document...</Typography>}
                            {error && <Typography color="error">{error}</Typography>}
                            {pdfUrl && (
                                <Document file={pdfUrl} onLoadSuccess={onDocumentLoadSuccess}>
                                    {Array.from(new Array(numPages), (el, index) => (
                                        <Page key={`page_${index + 1}`} pageNumber={index + 1} />
                                    ))}
                                </Document>
                            )}
                        </Box>
                    )}
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} variant="contained">
                    Close
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ApplicationViewer;
