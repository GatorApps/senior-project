import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, Button } from '@mui/material';
import { axiosPrivate } from '../../apis/backend';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

const CreateLabPopup = ({ open, onClose }) => {
    const [name, setName] = useState('');
    const [website, setWebsite] = useState('');
    const [description, setDescription] = useState('');
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async () => {
        setLoading(true);
        setError(null);

        try {
            const response = await axiosPrivate.post('/lab/profileEditor', {
                name,
                website,
                description,
                email
            });

            if (response.data.errCode === "0") {
                onClose(); // Close modal after successful submission
                navigate('/lab/profileEditor'); // Redirect to profile editor
            } else {
                setError(response.data.message || 'Error creating lab profile');
            }
        } catch (err) {
            setError(err.message || 'Error creating lab profile');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Create Lab Profile</DialogTitle>
            <DialogContent>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <TextField
                    fullWidth
                    margin="dense"
                    label="Lab Name"
                    variant="outlined"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    label="Lab Website"
                    variant="outlined"
                    value={website}
                    onChange={(e) => setWebsite(e.target.value)}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    label="Email"
                    variant="outlined"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <ReactQuill theme="snow" value={description} onChange={setDescription} />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} color="secondary" disabled={loading}>Cancel</Button>
                <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading}>
                    {loading ? 'Saving...' : 'Save'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default CreateLabPopup;
