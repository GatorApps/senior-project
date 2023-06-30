// https://www.npmjs.com/package/express-session
const session = require('express-session');
// https://www.npmjs.com/package/connect-mongodb-session
const MongoStore = require('connect-mongo');
const { DBglobal } = require('../config/dbConnections');
const { GLOBAL_SESSION_COOKIE_NAME, GLOBAL_SESSION_LIFESPAN } = require('../config/config');

const initializeUserSession = session({
  secret: JSON.parse(process.env.SESSION_COOKIE_SECRET),
  cookie: { domain: 'gatorapps.org', maxAge: GLOBAL_SESSION_LIFESPAN, secure: false },
  // store in MongoDB, use existing connection, TTL autoRemove handled separately
  store: MongoStore.create({ client: DBglobal.getClient(), dbName: 'dev_global', autoRemove: 'disabled' }),
  resave: false,
  saveUninitialized: false,
  name: GLOBAL_SESSION_COOKIE_NAME
});

module.exports = initializeUserSession