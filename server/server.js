require('dotenv').config();
const express = require('express');
// Express.js: expressjs.com
const app = express();
const path = require('path');
const cors = require('cors');
const { APP_CORS_OPTIONS, GLOBAL_CORS_OPTIONS } = require('./config/corsOptions');
const credentials = require('./middleware/credentials');
const checkAppAvailability = require('./middleware/checkAvailability');
const initializeReqProperties = require('./middleware/initializeReqProperties');
const initializeUserSession = require('./middleware/initializeUserSession');
const validateOrigin = require('./middleware/validateOrigin');
const validateUserAuth = require('./middleware/validateUserAuth');
const PORT = process.env.PORT || 8300;

// To simulate https locally
const https = require('https');
const fs = require('fs');
const httpsOptions = {
  key: fs.readFileSync('../../certs/templateapp.dev-local.gatorapps.org.key'),
  cert: fs.readFileSync('../../certs/templateapp.dev-local.gatorapps.org.cert')
};

// app.use(express.static(path.join(__dirname, './build')));

// app.use((req, res, next) => {
//   if (req.path.startsWith('/appApi/') || req.path.startsWith('/globalApi/')) return next();
//   res.sendFile(path.join(__dirname, './build/index.html'));
// });

// Connect to MongoDB mongodb.com
const { DBglobal, DBaccount, DBtemplate } = require('./config/dbConnections');

// Options credentials check and fetch cookies credentials requirement
app.use(credentials);

// CORS
// App APIs (only available to this apps' client)
app.use('/appApi/templateapp/', cors(APP_CORS_OPTIONS));
// Global APIs (available to all internal apps)
app.use('/globalApi/templateapp/', cors(GLOBAL_CORS_OPTIONS));

// Check if app is avaliable; only process requests if so
// TO DO: Resolve overlap with getAppAvailability in renderClientController
app.use(checkAppAvailability);

// Parse url and request body
app.use(express.urlencoded({ extended: false }));
// TO DO: Only support JSON body
app.use(express.json());
// Session cookie is currently handled by express-session
//app.use(cookieParser());

app.use(initializeReqProperties);

// Use express-session
// !--- ATTENTION: cookie set to secure: false for testing in Thunder Client. Change back to true for prod/testing in Chrome. ---!
// !--- ATTENTION: for prod, set cookie domain to .gatorapps.org ---!
app.use(initializeUserSession);

// Validate requesting app's origin and store app in req.foundApp
app.use(validateOrigin);
// Validate user auth status and store user in req.userAuth.authedUser
app.use(validateUserAuth);

// app.use((req, res) => {
//   console.log(req.userAuth);
// })

// Routes
// App APIs
//// Frontend render with dynamic data
app.use('/appApi/templateapp/renderClient', require('./routes/appApi/renderClient'));
app.use('/appApi/templateapp/appSettings', require('./routes/appApi/appSettings'));

// Global APIs
//// User auth requests from other internal apps


// HTTP Status Codes: https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
app.all('*', (req, res) => {
  res.sendStatus(404);
});


Promise.all([
  new Promise(resolve => DBglobal.once('open', resolve)),
  new Promise(resolve => DBaccount.once('open', resolve)),
  new Promise(resolve => DBtemplate.once('open', resolve))
]).then(() => {
  // Create index to automatically delete expired sessions in db
  DBglobal.db.collection('sessions').createIndex(
    { expires: 1 },
    { expireAfterSeconds: 0 }
  );
  console.log('Connected to MongoDB');
  // app.listen(PORT, () => console.log(`Server running on port ${PORT}`));

  // To simulate https locally
  https.createServer(httpsOptions, app).listen(PORT, () => console.log(`Server running on port ${PORT}`));
}).catch(error => {
  console.error('Error connecting to the database: ', error);
});
