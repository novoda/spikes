const google = require('googleapis');
const sheets = google.sheets('v4');

function GoogleSheets(config) {
  this.jwtClient = new google.auth.JWT(
    config.client_email,
    null,
    config.private_key,
    ['https://www.googleapis.com/auth/spreadsheets'],
    null
  );
}

GoogleSheets.prototype.authenticate = function() {
  const self = this;
  return new Promise(function(resolve, reject) {
    self.jwtClient.authorize(function (err, tokens) {
      if (err) {
        reject(err);
      } else {
        resolve();
      }
    });
  });
}

GoogleSheets.prototype.get = function(options) {
  const self = this;
  return new Promise(function(resolve, reject) {
    sheets.spreadsheets.values.batchGet(injectAuth(self, options), function(err, response) {
      if (err) {
        reject(err);
      } else {
        resolve(response);
      }
    });
  });
}

GoogleSheets.prototype.put = function(options) {
  const self = this;
  return new Promise(function(resolve, reject) {
    sheets.spreadsheets.values.batchUpdate(injectAuth(self, options), function(err, response) {
      if (err) {
        reject(err);
      } else {
        resolve(response);
      }
    });
  });
}

GoogleSheets.prototype.removeRow = function(options) {
  const self = this;
  return new Promise(function(resolve, reject) {
    sheets.spreadsheets.values.batchUpdate(injectAuth(self, options), function(err, response) {
      if (err) {
        reject(err);
      } else {
        resolve(response);
      }
    });
  });
}

function injectAuth(self, options) {
  options.auth = self.jwtClient;
  return options;
}

module.exports = GoogleSheets;
