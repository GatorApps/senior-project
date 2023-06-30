const mongoose = require('mongoose');

const DBglobal = mongoose.createConnection(process.env.DATABASE_URI, {
  useUnifiedTopology: true,
  useNewUrlParser: true,
  dbName: 'dev_global'
});

const DBaccount = mongoose.createConnection(process.env.DATABASE_URI, {
  useUnifiedTopology: true,
  useNewUrlParser: true,
  dbName: 'dev_account'
});

const DBtemplate = mongoose.createConnection(process.env.DATABASE_URI, {
  useUnifiedTopology: true,
  useNewUrlParser: true,
  dbName: 'dev_template'
});

module.exports = { DBglobal, DBaccount, DBtemplate };