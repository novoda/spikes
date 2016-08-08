var Slacker = require('./slacker/slacker.js');

const DASHBOARD_INTERVAL = 1000 * 5;

var Dashboard = function(token) {
  this.slacker = new Slacker(token);
  this.index = 0;
  this.rules = [
    require('./ci-wall').rule,
    require('./stackoverflow').rule
  ].concat(this.slacker.getRules());
}

Dashboard.prototype.start = function(callback) {
  var self = this;
  var updateLoop = function() {
    runRule(self, callback);
    incrementIndex(self);
    setTimeout(updateLoop, DASHBOARD_INTERVAL);
  }
  updateLoop();
}

function runRule(self, callback) {
  var ruleResult = self.rules[self.index]();
  if (ruleResult.payload) {
    callback(ruleResult);
  } else {
    // TODO do something
  }
}

function incrementIndex(self) {
  if (self.index >= self.rules.length - 1) {
    self.index = 0;
  } else {
    self.index++;
  }
}

Dashboard.prototype.forceUpdate = function(callback) {
  runRule(this, callback);
}

module.exports = Dashboard;
