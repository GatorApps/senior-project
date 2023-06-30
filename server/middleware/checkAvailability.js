const checkAppAvailability = (req, res, next) => {
  // return res.status(503).json({ 'errCode': '-', 'errMsg': 'App is temporarily unavailable due to necessary maintenance.' });
  return next();
}

module.exports = checkAppAvailability