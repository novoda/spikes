var Slacker = require('./slacker/slacker.js');

const DASHBOARD_INTERVAL = 1000 * 5;

var Dashboard = function(token) {
  this.slacker = new Slacker(token);
  this.index = 0;
  this.rules = [
    require('./ci-wall').rule
  ].concat(this.slacker.getRules());
}

Dashboard.prototype.start = function(callback) {
  var self = this;
  var updateLoop = function() {
    var ruleResult = self.rules[self.index]();
    incrementIndex(self);
    if (ruleResult.payload) {
      callback(ruleResult);
    } else {
      // TODO do something
    }
    setTimeout(updateLoop, DASHBOARD_INTERVAL);
  }
  updateLoop();
}

function incrementIndex(self) {
  if (self.index >= self.rules.length - 1) {
    self.index = 0;
  } else {
    self.index++;
  }
}

module.exports = Dashboard;
