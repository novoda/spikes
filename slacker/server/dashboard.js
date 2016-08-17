var Slacker = require('./slacker/slacker.js');

const DASHBOARD_INTERVAL = 1000 * 5;

var Dashboard = function(token) {
  this.slacker = new Slacker(token);
  this.index = 0;
  this.widgets = [
    require('./ci-wall')
  ]
}

Dashboard.prototype.start = function(callback) {
  var self = this;
  var updateLoop = function() {
    let timeoutInterval = getTimeoutInterval(self);
    log('running rule ' + self.index + ' for ' + timeoutInterval + 'ms...')
    runRule(self, callback);
    incrementIndex(self);
    setTimeout(updateLoop, timeoutInterval);
  }
  updateLoop();
}

function runRule(self, callback) {
  self.widgets[self.index].rule().then(function(result) {
    if (result.payload) {
      callback(result);
    } else {
      // TODO do something
    }
  });
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

Dashboard.prototype.forceUpdate = function(callback) {
  runRule(this, callback);
}

function log(msg, tag) {
  let separator = ''
  if (tag) {
    separator = '[' + tag + ']'
  }
  console.log(Math.floor(Date.now()/1000) + separator + ': ' + msg)
}

module.exports = Dashboard;
