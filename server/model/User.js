const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const { DBaccount } = require('../config/dbConnections');

const userSchema = new Schema({
  opid: {
    type: String,
    required: true,
    unique: true
  },
  registerTimestamp: {
    type: Date,
    required: true
  },
  roles: {
    type: [Number],
    required: true
  },
  firstName: {
    type: String,
    require: true
  },
  lastName: {
    type: String,
    require: true
  },
  nickname: String,
  emails: [String],
  sessions: [{
    _id: false,
    sessionID: String,
    signInTimeStamp: Date
    // TODO: Automatically remove expired sessions
  }]
});

module.exports = DBaccount.model('User', userSchema);