const FRONTEND_HOST = 'https://templateapp.dev-local.gatorapps.org:3300';

// Global session
// Session id cookie name
const GLOBAL_SESSION_COOKIE_NAME = 'GATORAPPS_GLOBAL_SID';
// Session lifespan in milliseconds
const GLOBAL_SESSION_LIFESPAN = 1000 * 60 * 60 * 12; // 12 hours
// Session token (stored with user session in DB to validate session integrity) lifespan
//// For sign in status. Can have sign in status expire before global session expire. Currently same as session lifespan
const GLOBAL_USER_AUTH_TOKEN_LIFESPAN = '12h'; // 12 hours
// Maximum allowed number of simultaneous sessions on web app for each user
const MAX_WEB_SESSIONS = 2;

// /globalApi/account/userAuth/getUserInfo
// Default userInfo attributes returned for getUserInfo call
// Will not return an attribute if the requesting app does not have permission to access it
const DEFAULT_GETUSERINFO_SCOPE = ['roles', 'nickname', 'firstName', 'lastName', 'emails'];

// Oauth 2 clients, DO NOT store client secrets here
//// Google
GOOGLE_CLIENT_ID = process.env.GOOGLE_CLIENT_ID;
GOOGLE_REDIRECT_URIS = [`${FRONTEND_HOST}/signin/callback/ufgoogle`];

module.exports = {
  FRONTEND_HOST, GLOBAL_SESSION_COOKIE_NAME, GLOBAL_SESSION_LIFESPAN, GLOBAL_USER_AUTH_TOKEN_LIFESPAN, MAX_WEB_SESSIONS,
  DEFAULT_GETUSERINFO_SCOPE, GOOGLE_CLIENT_ID, GOOGLE_REDIRECT_URIS
};