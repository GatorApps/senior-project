const checkAppAvailability = async (req, res, next) => {
  const reqApp = req.reqApp;

  if (reqApp?.alert?.maintenanceMode === true) return res.status(503).json({
    "errCode": "-",
    "errMsg": "App is temporarily unavailable due to necessary maintenance.",
    "payload": {
      appAlert: reqApp.alert,
    }
  });

  return next();
}

module.exports = checkAppAvailability