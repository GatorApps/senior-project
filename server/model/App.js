const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const { DBglobal } = require('../config/dbConnections');

const appSchema = new Schema({
  code: {
    type: Number,
    required: [true, `App code is required`],
    unique: true
  },
  name: {
    type: String,
    required: [true, `App name is required`],
    unique: true
  },
  displayName: {
    type: String,
    required: [true, `App displayName is required`]
  },
  origins: {
    type: [String],
  },
  userInfoScope: [String],
  authOptions: [String],
  alert: {
    alertId: {
      type: String,
      required: true
    },
    displayAlert: Boolean,
    maintenanceMode: Boolean,
    severity: {
      type: String,
    },
    title: {
      type: String,
    },
    message: {
      type: String,
    },
    actions: [{
      title: String,
      action: String,
    }]
  },
});

module.exports = DBglobal.model('App', appSchema);