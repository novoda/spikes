const Moment = require('moment');

function XmasCountdown() {}

XmasCountdown.prototype.rank = 1;

XmasCountdown.prototype.rule = function() {
  return Promise.resolve({});
}

module.exports = XmasCountdown;
