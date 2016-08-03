var RtmClient = require('@slack/client').RtmClient;
var RTM_EVENTS = require('@slack/client').RTM_EVENTS;
var RTM_CLIENT_EVENTS = require('@slack/client').CLIENT_EVENTS.RTM;
var MemoryDataStore = require('@slack/client').MemoryDataStore;

var mostRecentQuestionRule = require('./most-recent-question.js').rule;
var mostCommonWordsRule = require('./most-common-words.js').rule;

var rules = [
  require('./biggest-slacker.js').rule,
  require('./most-active-channel.js').rule,
  require('./thanks.js').rule,
  require('./gallery').rule
]

var Slacker = function(token) {
  this.messages = [];
  this.index = 0;
  this.ruleIndex = 0;
  this.rtm = new RtmClient(
    token,
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

Slacker.prototype.moveToNext = function() {
  incrementRuleIndex(this);
};

function incrementRuleIndex(self) {
  if (self.ruleIndex >= (rules.length - 1)) {
    self.ruleIndex = 0;
  } else {
    self.ruleIndex++;
  }
}

Slacker.prototype.getCurrentStat = function(callback) {
  if (this.messages.length > 0) {
    var result = rules[this.ruleIndex](this.rtm.dataStore, this.messages);
    callback(result);
  }
};

Slacker.prototype.getRules = function() {
  var self = this;
  return rules.map(each => {
    return function() {
      return each(self.rtm.dataStore, self.messages);
    }
  });
}

module.exports = Slacker;
