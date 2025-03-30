import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, Button } from '@mui/material';
import { axiosPrivate } from '../../apis/backend';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { FormControl, MenuItem, Select, Typography } from '@mui/material';
import InputLabel from '@mui/material/InputLabel';

// Response format
const response = {
    "errCode": "0",
    "payload": {
        "lab": {
            "id": "67dde81544c53033691a78f9",
            "users": [
                {
                    "opid": "fd1d4d15-10f3-4285-879f-4ccb1944d6f3",
                    "role": "Admin"
                }
            ],
            "name": "Eric's Test Lab 892",
            "department": null,
            "website": "https://testlab.gatorapps.org",
            "email": "testEmail@gmail.com",
            "description": "<p><strong>Description</strong><br>This is a test lab creation for students interested in mechanical, robots, hardware, ai. We work with testing"
        }
    }
}

const CreateLabPopup = ({ open, onClose, labs }) => {
    const [name, setName] = useState('');
    const [website, setWebsite] = useState('');
    const [description, setDescription] = useState('');
    const [email, setEmail] = useState('');
    const [department, setDepartment] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const [labId, setLabId] = useState('New Lab');

    const fetchExistingLab = async (labId) => {
        try {
            const response = await axiosPrivate.get(`/lab/profileEditor?labId=${labId}`);
            if (response.data.errCode === "0") {
                const lab = response.data.payload.lab;
                setName(lab.name || '');
                setWebsite(lab.website || '');
                setDescription(lab.description || '');
                setEmail(lab.email || '');
                setDepartment(lab.department || '');
            } else {
                setError(response.data.message || 'Error fetching lab profile');
            }
        } catch (err) {
            setError(err.message || 'Error fetching lab profile');
        }
    };

    const handleLabChange = (e) => {
        setLabId(e.target.value);
        if (e.target.value !== 'New Lab') {
            fetchExistingLab(e.target.value);
        } else {
            setName('');
            setWebsite('');
            setDescription('');
            setEmail('');
            setDepartment('');
        }
    };

    const handleSubmit = async () => {
        setLoading(true);
        setError(null);

        const labData = {
            name,
            website,
            description,
            email,
            department,
        };

        // If editing an existing lab, include the labId in the body
        if (labId !== 'New Lab') {
            labData.id = labId;
        }

        try {
            let response;

            if (labId === 'New Lab') {
                // Create a new lab with a POST request
                response = await axiosPrivate.post('/lab/profileEditor', labData);
            } else {
                // Update an existing lab with a PUT request
                response = await axiosPrivate.put('/lab/profileEditor', labData);
            }

            if (response.data.errCode === "0") {
                onClose(); // Close modal after successful submission
            } else {
                setError(response.data.message || 'Error processing lab profile');
            }
        } catch (err) {
            setError(err.message || 'Error processing lab profile');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Create/Edit Lab Profile</DialogTitle>
            <DialogContent>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <FormControl fullWidth sx={{ marginTop: 4, marginBottom: 8, backgroundColor: 'white' }}>
                    <InputLabel id="lab-select-label">Select Lab</InputLabel>
                    <Select
                        label="Select Lab"
                        value={labId || ''}  // Set the value to '' by default to show "New Lab"
                        onChange={(e) => handleLabChange(e)}
                        labelId="lab-select-label"
                    >
                        <MenuItem value='New Lab'>New Lab</MenuItem> {/* Default value */}
                        {labs.length > 0 &&
                            labs.map((lab) => (
                                <MenuItem key={lab.labId} value={lab.labId}>{lab.labName}</MenuItem>
                            ))
                        }
                    </Select>
                </FormControl>
                <TextField
                    fullWidth
                    margin="dense"
                    label="Lab Name"
                    variant="outlined"
                    value={name || ''}
                    onChange={(e) => setName(e.target.value)}
                    sx={{ marginBottom: 3 }}  // Adds a comfortable margin below the text field
                />

                <TextField
                    fullWidth
                    margin="dense"
                    label="Lab Website"
                    variant="outlined"
                    value={website || ''}
                    onChange={(e) => setWebsite(e.target.value)}
                    sx={{ marginBottom: 3 }}  // Adds a comfortable margin below the text field
                />

                <TextField
                    fullWidth
                    margin="dense"
                    label="Email"
                    variant="outlined"
                    value={email || ''}
                    onChange={(e) => setEmail(e.target.value)}
                    sx={{ marginBottom: 4 }}  // Adds more margin to separate from the next section
                />

                <TextField
                    fullWidth
                    margin="dense"
                    label="Department"
                    variant="outlined"
                    value={department || ''}
                    onChange={(e) => setDepartment(e.target.value)}
                    sx={{ marginBottom: 4 }}  // Adds more margin to separate from the next section
                />

                <Typography variant='h6' marginLeft={1} marginBottom={1} marginTop={2}>
                    Description
                </Typography>

                <ReactQuill
                    theme="snow"
                    value={description || ''}
                    onChange={setDescription}
                    style={{ marginBottom: '24px' }}  // Adds margin below ReactQuill for space
                />

            </DialogContent>
            <DialogActions sx={{ margin: 2, padding: 2 }}>
                <Button onClick={onClose} color="error" variant='outlined' disabled={loading}>Cancel</Button>
                <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading}>
                    {loading ? 'Saving...' : 'Save'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default CreateLabPopup;
