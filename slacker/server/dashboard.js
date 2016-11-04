const Slacker = require('./slacker/slacker.js');

const DASHBOARD_INTERVAL = 1000 * 30;
const DASHBOARD_ERROR_INTERVAL = 1000 * 1;

function Dashboard(token) {
  this.slacker = new Slacker(token);
  this.index = 0;
  this.widgets = [
    require('./ci-wall'),
    require('./coverage'),
    require('./stackoverflow'),
    require('./reviews')
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
