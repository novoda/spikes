const moment = require('moment');

function XmasCountdown() {}

XmasCountdown.prototype.rank = 1;

XmasCountdown.prototype.rule = function() {
  const now = moment();
  const christmas = moment([2016, 11, 25]);
  const daysUntilChristmas = christmas.diff(now, 'days');
  return Promise.resolve({
    widgetKey: 'xmasCountdown',
    payload: {
      daysUntilChristmas: daysUntilChristmas
    }
  });
}

module.exports = XmasCountdown;
