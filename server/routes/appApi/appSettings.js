const express = require('express');
const router = express.Router();
const appSettingsController = require('../../controllers/appSettingsController');

router.get('/userSettings', appSettingsController.getUserSettings);

module.exports = router;