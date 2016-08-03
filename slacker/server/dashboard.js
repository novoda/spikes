var Slacker = require('./slacker.js');

const DASHBOARD_INTERVAL = 1000 * 5;

var Dashboard = function(token) {
  this.slacker = new Slacker(token);
  this.index = 0;
  this.rules = [
    ciRule
  ].concat(this.slacker.getRules());
}

Dashboard.prototype.start = function(callback) {
  var self = this;
  var updateLoop = function() {
    callback(self.rules[self.index]());

    if (self.index >= self.rules.length) {
      self.index = 0;
    } else {
      self.index++;
    }
    setTimeout(updateLoop, DASHBOARD_INTERVAL);
  }
  updateLoop();
}

function ciRule() {
  return {
    thingKey: 'ciWall',
    payload: 'https://ci.novoda.com//plugin/jenkinswalldisplay/walldisplay.html?viewName=Active&jenkinsUrl=https%3A%2F%2Fci.novoda.com%2F'
  };
}

module.exports = Dashboard;
