var Slacker = require('./slacker/slacker.js');

const DASHBOARD_INTERVAL = 1000 * 5;

var Dashboard = function(token) {
  this.slacker = new Slacker(token);
  this.index = 0;
  this.widgets = [
    require('./ci-wall')
  ]
}

function update(callback) {
  var self = this;
  var updateLoop = function(self) {
    getCurrentRule(self).then(result => {
        callback(result)
        incrementIndex(self);
        setTimeout(updateLoop, getTimeoutInterval(self));
      }).catch(err => {
        incrementIndex(self);
        setTimeout(updateLoop, 1);
    })
  }
}

Dashboard.prototype.start = update;

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

Dashboard.prototype.forceUpdate = function(callback) {
  update(callback);
}

function log(msg, tag) {
  let separator = ''
  if (tag) {
    separator = '[' + tag + ']'
  }
  console.log(Math.floor(Date.now()/1000) + separator + ': ' + msg)
}

module.exports = Dashboard;
