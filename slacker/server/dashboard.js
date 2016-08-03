var Slacker = require('./slacker.js');

const DASHBOARD_INTERVAL = 1000 * 5;

var Dashboard = function(token) {
  this.slacker = new Slacker(token);
  this.index = 0;
}

Dashboard.prototype.start = function(callback) {
  var self = this;
  var updateLoop = function() {
    if (self.index % 3 == 0) {
        callback({ thingKey: 'ciWall'});
    } else {
      self.slacker.moveToNext();
      self.slacker.getCurrentStat(callback);
    }
    self.index++;
    setTimeout(updateLoop, DASHBOARD_INTERVAL);
  }
  updateLoop();
}

Dashboard.prototype.getCurrentStat = function(callback) {
  if (this.index % 3 == 0) {
      callback({ thingKey: 'ciWall'});
  } else {
    this.slacker.moveToNext();
    this.slacker.getCurrentStat(callback);
  }
};

module.exports = Dashboard;
