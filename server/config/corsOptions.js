const APP_ORIGINS = [
  'https://templateapp.dev-local.gatorapps.org:3300'
];

const GLOBAL_ORIGINS = [
  'https://templateapp.dev-local.gatorapps.org:3300'
];

const APP_CORS_OPTIONS = {
  origin: (origin, callback) => {
    // Add || !origin to allow REST or server-to-server requests
    // Recommend asynchronous for advanced access control and external apis
    if (APP_ORIGINS.includes(origin) || !origin) {
      callback(null, true)
    } else {
      callback(new Error());
    }
  },
  optionsSuccessStatus: 200
};

const GLOBAL_CORS_OPTIONS = {
  origin: (origin, callback) => {
    // Add || !origin to allow REST or server-to-server requests
    // Recommend asynchronous for advanced access control and external apis
    if (APP_ORIGINS.includes(origin) || !origin) {
      callback(null, true)
    } else {
      callback(new Error());
    }
  },
  optionsSuccessStatus: 200
};

module.exports = { GLOBAL_ORIGINS, APP_CORS_OPTIONS, GLOBAL_CORS_OPTIONS };