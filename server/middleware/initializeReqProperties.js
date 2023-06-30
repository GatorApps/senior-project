const initializeReqProperties = (req, res, next) => {
  // Global client session
  delete req.session;
  delete req.sessionID;

  // Global requesting app with authorized origin
  delete req.reqApp;

  // Global client auth, stores authed user or auth error
  delete req.userAuth;

  next();
}

module.exports = initializeReqProperties