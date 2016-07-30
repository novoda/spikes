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
  require('./most-recent-gif.js').rule
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

Slacker.prototype.forceUpdate = function(callback) {
  if (this.messages.length > 0) {
    callback(runRules(this.rtm.dataStore, this.messages, this.ruleIndex));
    if (this.ruleIndex >= (rules.length - 1)) {
      this.ruleIndex = 0;
    } else {
      this.ruleIndex++;
    }
  }
};

function runRules(dataStore, messages, ruleIndex) {
  // most [x word] usages | lol etc
  // common words
  // most used emoticon
  // most unused emoticon
  // quote - find text with " foo bar "
  // user with most channel spread | most channels, most messages
  // user most dedicated to a single channel (by character) | least channels least messages
  // biggest laugher | ha* or lol or laughing emoticon
  // most questions
  // most @here
  // most I/Me user
  // biggest guess user
  // most actually/btw
  // most boss pings @carl / @kevin
  // most use of symbols rather than a-z
  // longest word
  // most negative
  // first person to mention monday on monday
  // first person to mention going home
  // first person to mention lunch
  // dynamic duo, people who talk to each other
  // tourist abroad, active in an office chat that they aren't based in
  return rules[ruleIndex](dataStore, messages);
}

module.exports = Slacker;
