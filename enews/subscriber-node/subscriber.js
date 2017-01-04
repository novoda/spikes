const GoogleSheets = require('./google-sheets');
const validator = require('validator');

const RESULT_ALREADY_SUBSCRIBED = 'already subscribed'
const RESULT_ADDED_SUCCESSFULLY = 'email added successfully'
const RESULT_INVALID_EMAIL = 'email is invalid'

const sheetname = 'Sheet1';
const nameRow = "A1:A";

function Subscriber(config) {
  this.googleSheets = new GoogleSheets(config);
  this.spreadsheet = config.spreadsheet;
}

Subscriber.prototype.add = function(email) {
  const self = this;
  if (validator.isEmail(email)) {
    return addToSheet(self, email);
  } else {
    return Promise.reject(RESULT_INVALID_EMAIL);
  }
}

function addToSheet(self, email) {
  return self.getAll().then(subscribers => {
      if (subscribers.length > 0) {
        if (subscribers.includes(email)) {
          return resolveResult(RESULT_ALREADY_SUBSCRIBED)
        }
      }
      return addEmailToList(self, subscribers.length, email).then(() => {
        return resolveResult(RESULT_ADDED_SUCCESSFULLY);
      });
    });
}

function addEmailToList(self, lastPosition, email) {
  const cell = positionAsListCell(lastPosition);
  const resource = {
    valueInputOption: 'USER_ENTERED',
    data: {
      range: `${sheetname}!${cell}:${cell}`,
      majorDimension: 'ROWS',
      values: [[email]]
    }
  };
  return self.googleSheets.put({
    spreadsheetId: self.spreadsheet,
    resource: resource
  });
}

function positionAsListCell(position) {
  return `A${position}`;
}

function flatten(array) {
  return [].concat.apply([], array);
}

function resolveResult(result) {
  return Promise.resolve(result);
}

Subscriber.prototype.getAll = function() {
  const self = this;
  return self.googleSheets.authenticate()
    .then(() => {
      const options = {
        spreadsheetId: self.spreadsheet,
        ranges: [`${sheetname}!${nameRow}`]
      }
      return self.googleSheets.get(options);
    }).then(toSubscribers);
}

function toSubscribers(result) {
  const values = result.valueRanges[0].values;
  if (values) {
      return Promise.resolve(flatten(values));
  } else {
    return Promise.resolve([]);
  }
}

module.exports = Subscriber;
