const App = require('../model/App');

const validateOrigin = async (req, res, next) => {
  const requestingApp = req.header('GATORAPPS_app');
  if (!requestingApp) return res.status(400).json({ 'errCode': '-', 'errMsg': 'Missing requesting app' });

  const origin = req.header('origin');
  // if (!origin) return res.status(400).json({ 'errCode': '-', 'errMsg': 'Missing request origin' });

  let foundApp;
  try {
    foundApp = await App.findOne({ name: requestingApp }).exec();
  } catch (err) {
    return res.status(500).json({ 'errCode': '-', 'errMsg': 'Unable to look up requesting app' });
  }

  if (!foundApp) return res.status(400).json({ 'errCode': '-', 'errMsg': 'Requesting app does not exist' });

  // if (!foundApp.origins.includes(origin)) return res.status(403).json({ 'errCode': '-', 'errMsg': 'Unauthorized or mismatched origin' });

  req.reqApp = foundApp;
  next();
};

module.exports = validateOrigin