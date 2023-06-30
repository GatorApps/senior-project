const checkAppAvailability = async (req, res, next) => {
  const reqApp = JSON.parse(JSON.stringify(req.reqApp));

  if (!reqApp?.alert?.displayAlert) reqApp.alert = { displayAlert: false };

  if (reqApp?.alert?.maintenanceMode === true) return res.status(503).json({
    "errCode": "-",
    "errMsg": "App is temporarily unavailable due to necessary maintenance.",
    "payload": { app: { name: reqApp.name, displayName: reqApp.displayName, alert: reqApp.alert } }
  });

  return next();
}

module.exports = checkAppAvailability