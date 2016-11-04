const Slacker = require('./widget/slacker/slacker.js');
const Coverage = require('./widget/coverage');

const DASHBOARD_INTERVAL = 1000 * 30;
const DASHBOARD_ERROR_INTERVAL = 1000 * 1;

function Dashboard(config) {
  this.slacker = new Slacker(config.widgets.slack);
  this.index = 0;
  this.widgets = [
    require('./widget/ci-wall'),
    new Coverage(config.widgets.sonar),
    require('./widget/stackoverflow'),
    require('./widget/reviews')
  ].concat(this.slacker.getRules());
}

Dashboard.prototype.start = function(listener) {
  update(this, listener);
}

function update(self, listener) {
  const updateLoop = function() {
    const rule = getCurrentRule(self);
    rule().then(result => {
        listener(result)
        incrementIndex(self);
        setTimeout(updateLoop, getTimeoutInterval(self));
      }).catch(err => {
        console.log(err);
        incrementIndex(self);
        setTimeout(updateLoop, DASHBOARD_ERROR_INTERVAL);
    })
  }
  updateLoop();
}

function getCurrentRule(self) {
  return self.widgets[self.index].rule;
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
