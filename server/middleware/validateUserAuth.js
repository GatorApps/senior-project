const jwt = require('jsonwebtoken');
const User = require('../model/User');

const validateUserAuth = async (req, res, next) => {
  // Check userAuth info exists in session
  const userAuth = req?.session?.userAuth;
  if (!userAuth) {
    req.userAuth = {
      error: { status: 403, errCode: '-', errMsg: 'Session missing userAuth info' }
    };
    return next();
  }

  // Check opid and userAuthToken exists in userAuth
  const opid = userAuth?.opid;
  const userAuthToken = userAuth?.token;
  if (!opid || !userAuthToken) {
    req.userAuth = {
      error: { status: 403, errCode: '-', errMsg: 'Incomplete userAuth info' }
    };
    return next();
  }

  try {
    await jwt.verify(
      userAuthToken,
      process.env.USR_AUTH_TOKEN_PUBLIC_KEY.replace(/\\n/g, '\n'),
      { algorithm: 'ES256', ignoreExpiration: true },
      async (err, decoded) => {
        // Validate userAuthToken
        if (err || !decoded) {
          req.userAuth = {
            error: { status: 403, errCode: '-', errMsg: 'Invalid userAuthToken' }
          };
          return next();
        }
        if (!decoded.opid || !decoded.sessionID || !decoded.signInTimeStamp) {
          req.userAuth = {
            error: { status: 403, errCode: '-', errMsg: 'Incomplete userAuthToken' }
          };
          return next();
        }

        // Check opid, sessionID in userAuthToken matches those of requesting client
        if (decoded.opid !== userAuth.opid) {
          req.userAuth = {
            error: { status: 403, errCode: '-', errMsg: 'Token and client opid mismatch' }
          };
          return next();
        }
        if (decoded.sessionID !== req.sessionID) {
          req.userAuth = {
            error: { status: 403, errCode: '-', errMsg: 'Token and client sessionID mismatch' }
          };
          return next();
        }

        // Find user with opid
        const foundUser = await User.findOne({ opid: decoded.opid }).exec();
        if (!foundUser) {
          req.userAuth = {
            error: { status: 403, errCode: '-', errMsg: 'Invalid user opid in userAuthToken' }
          };
          return next();
        }

        // Check session is associated with foundUser
        const foundSession = await foundUser.sessions.find(session => session.sessionID === decoded.sessionID);
        if (!foundSession) {
          req.userAuth = {
            error: { status: 403, errCode: '-', errMsg: 'Client and user sessionID mismatch' }
          };
          return next();
        }

        // Check signInTimeStamp match as stored with foundUser
        if (String(new Date(decoded.signInTimeStamp)) !== String(new Date(foundSession.signInTimeStamp))) {
          req.userAuth = {
            error: { status: 403, errCode: '-', errMsg: 'Sign in time stamp mismatch' }
          };
          return next();
        }

        // Check auth has not expired
        const currentTimestamp = Math.floor(Date.now() / 1000);
        if (decoded.exp && decoded.exp > currentTimestamp) {
          // If auth is active, store foundUser in req.userAuth
          req.userAuth = {
            authedUser: foundUser,
            error: { errCode: '0' }
          };
        } else {
          // If auth expired, store foundUser as expiredUser in error
          req.userAuth = {
            error: { status: 403, errCode: '-', errMsg: 'Auth session has expired', expiredUser: foundUser }
          };
        }
        return next();
      }
    );
  } catch (err) {
    req.userAuth = {
      error: { status: 500, errCode: '-', errMsg: 'Unable to validate user auth session' }
    };
    return next();
  }
};

module.exports = validateUserAuth