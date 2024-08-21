const express = require('express');
const router = express.Router();
const renderClientController = require('../../controllers/renderClientController');

router.get('/appAlert', renderClientController.getAppAlert);
router.get('/appInfo', renderClientController.getAppInfo);
router.get('/leftMenuItems', renderClientController.getLeftMenuItems);

module.exports = router;