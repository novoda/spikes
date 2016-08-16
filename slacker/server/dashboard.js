var Slacker = require('./slacker/slacker.js');

const DASHBOARD_INTERVAL = 1000 * 5;

var Dashboard = function(token) {
  this.slacker = new Slacker(token);
  this.index = 0;
  this.rules = [
    require('./stackoverflow').rule
    //require('./ci-wall').rule
  ]//.concat(this.slacker.getRules());
}

// callback = io.emit('message', data); [from: server.js]
// callback() will emit the result of each rule's operation/execution, i.e., 
//  the html with data

Dashboard.prototype.start = function(callback) {
  var self = this;
  var updateLoop = function() {
    runRule(self, callback);
    incrementIndex(self);
    self.timeout = setTimeout(updateLoop, DASHBOARD_INTERVAL);
  }
  updateLoop();
}

function runRule(self, callback) {
  self.rules[self.index]().then(function(result) {
    if (result.payload) {
      callback(result);
    } else {
      // TODO do something
    }
  });
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
