var RtmClient = require('@slack/client').RtmClient;
var RTM_EVENTS = require('@slack/client').RTM_EVENTS;
var RTM_CLIENT_EVENTS = require('@slack/client').CLIENT_EVENTS.RTM;
var MemoryDataStore = require('@slack/client').MemoryDataStore;

var rules = [
  require('./biggest-slacker.js'),
  require('./most-active-channel.js'),
  require('./thanks.js'),
  require('./gallery.js')
]

var Slacker = function(config) {
  this.messages = [];
  this.index = 0;
  this.ruleIndex = 0;
  this.rtm = new RtmClient(
    config.token,
    { dataStore: new MemoryDataStore() }
  );
  this.rtm.start();
  this.rtm.on(RTM_EVENTS.MESSAGE, messageHandler(this));
}

function messageHandler(self, callback) {
  return function(message) {
    if (ignored(message)) {
      console.log('message ignored');
      return;
    }
    if (self.index === 100) {
      self.index = 0;
    }
    self.messages.splice(self.index, 0 , message);
    self.index++;
  }
}

function ignored(message) {
  return message.type !== 'message' || message.bot_id || !message.text;
}

Slacker.prototype.getRules = function() {
  var self = this;
  return rules.map(each => {
    return {
      rule: function() {
        return each.rule(self.rtm.dataStore, self.messages)
      },
      rank: 1
    };
  });
}

module.exports = Slacker;
