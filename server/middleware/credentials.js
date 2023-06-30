const { GLOBAL_ORIGINS } = require('../config/corsOptions');

const credentials = (req, res, next) => {
  const origin = req.headers.origin;
  if (GLOBAL_ORIGINS.includes(origin)) {
    res.header('Access-Control-Allow-Credentials', true);
  }
  next();
}

module.exports = credentials;