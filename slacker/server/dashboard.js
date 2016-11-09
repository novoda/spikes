const Slacker = require('./widget/slacker/slacker');
const Coverage = require('./widget/coverage');
const StackOverflow = require('./widget/stackoverflow');
const Reviews = require('./widget/reviews');
const XmasCountdown = require('./widget/xmas-countdown');

const DASHBOARD_INTERVAL = 1000 * 30;
const DASHBOARD_ERROR_INTERVAL = 1000 * 1;

function Dashboard(config) {
  this.slacker = new Slacker(config.slack);
  this.index = 0;
  this.widgets = [
    require('./widget/ci-wall'),
    new Coverage(config.sonar),
    new StackOverflow(),
    new Reviews(),
    new XmasCountdown()
  ].concat(this.slacker.getRules());
}

Dashboard.prototype.start = function(listener) {
  updateLoop(this, listener)();
}

function updateLoop(self, listener) {
  return function() {
    getCurrentRule(self).then(result => {
        listener(result)
        incrementIndex(self);
        setTimeout(updateLoop(self, listener), getTimeoutInterval(self));
      }).catch(err => {
        console.log(err);
        incrementIndex(self);
        setTimeout(updateLoop(self, listener), DASHBOARD_ERROR_INTERVAL);
    });
  };
}

function getCurrentRule(self) {
  return self.widgets[self.index].rule();
}

function incrementIndex(self) {
  if (self.index >= self.widgets.length - 1) {
    self.index = 0;
  } else {
    self.index++;
  }
}

function getTimeoutInterval(self) {
  return DASHBOARD_INTERVAL * self.widgets[self.index].rank
}

module.exports = Dashboard;
