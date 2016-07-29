var RtmClient = require('@slack/client').RtmClient;
var RTM_EVENTS = require('@slack/client').RTM_EVENTS;
var RTM_CLIENT_EVENTS = require('@slack/client').CLIENT_EVENTS.RTM;
var MemoryDataStore = require('@slack/client').MemoryDataStore;

var biggestSlackerRule = require('./biggest-slacker.js').rule;
var mostActiveChannelRule = require('./most-active-channel.js').rule;
var mostRecentQuestionRule = require('./most-recent-question.js').rule;
var mostRecentGifRule = require('./most-recent-gif.js').rule;
var mostCommonWordsRule = require('./most-common-words.js').rule;
var thanksRule = require('./thanks.js').rule;

var Slacker = function(token) {
  this.messages = [];
  this.index = 0;
  this.rtm = new RtmClient(
    token,
    { dataStore: new MemoryDataStore() }
  );
}

Slacker.prototype.forceUpdate = function(callback) {
  if (this.messages.length > 0) {
    runRules(this.rtm.dataStore, this.messages, callback);
  }
};

Slacker.prototype.startListening = function(callback) {
    this.rtm.start();
    this.rtm.on(RTM_EVENTS.MESSAGE, messageHandler(this, callback));
};

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

    runRules(self.rtm.dataStore, self.messages, callback);
  }
}

function ignored(message) {
  return message.type !== 'message' || message.bot_id || !message.text;
}

function runRules(dataStore, messages, callback) {
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

  callback({
    biggestSlacker: biggestSlacker(dataStore, messages),
    mostActiveChannel: mostActiveChannel(dataStore, messages),
    mostRecentGif: mostRecentGif(dataStore, messages),
    mostCommonWord: mostCommonWordsRule(messages),
    mostRecentQuestion: mostRecentQuestionRule(messages),
    thanks: thanks(dataStore, messages),
  });
}

function biggestSlacker(dataStore, messages) {
  var biggestSlacker = biggestSlackerRule(messages);
  var user = dataStore.getUserById(biggestSlacker.key);
  return { user: user, payload: biggestSlacker };
}

function mostActiveChannel(dataStore, messages) {
  var mostActiveChannel = mostActiveChannelRule(messages);
  var channel = dataStore.getChannelById(mostActiveChannel.key);
  return channel;
}

function mostRecentGif(dataStore, messages) {
  var mostRecentGif = mostRecentGifRule(messages);
  if (mostRecentGif) {
    var user = dataStore.getUserById(mostRecentGif.user);
    return {
      user: user,
      payload: mostRecentGif
    };
  } else {
    return null;
  }
}

function thanks(dataStore, messages) {
  var channel = dataStore.getChannelByName('thanks');
  var randomThankYou = thanksRule(channel.id, messages);
  if (randomThankYou) {
    var user = dataStore.getUserById(randomThankYou.user);
    return {user: user, payload: randomThankYou};
  }
  else {
    return null;
  }
}

module.exports = Slacker;
