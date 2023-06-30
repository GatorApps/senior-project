const requireUserAuth = async (req, res, next) => {
  const authError = req?.userAuth?.error;
  if (!authError) return res.status(500).json({ 'errCode': '-', 'errMsg': 'Internal server error' });

  if (authError.errCode !== '0') {
    if (!authError.status || !authError.errCode || !authError.errMsg) return res.status(500).json({ 'errCode': '-', 'errMsg': 'Internal server error' });
    return res.status(authError.status).json({ 'errCode': authError.errCode, 'errMsg': authError.errMsg });
  };

  if (!req?.userAuth?.authedUser) return res.status(500).json({ 'errCode': '-', 'errMsg': 'Internal server error' });

  return next();
};

module.exports = requireUserAuth