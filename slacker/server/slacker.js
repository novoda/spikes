module.exports = Slacker;

var RtmClient = require('@slack/client').RtmClient;
var RTM_EVENTS = require('@slack/client').RTM_EVENTS;
var RTM_CLIENT_EVENTS = require('@slack/client').CLIENT_EVENTS.RTM;
var MemoryDataStore = require('@slack/client').MemoryDataStore;

var biggestSlackerRule = require('./biggest-slacker.js').rule;
var mostActiveChannelRule = require('./most-active-channel.js').rule;
var mostRecentQuestionRule = require('./most-recent-question.js').rule;
var mostRecentGifRule = require('./most-recent-gif.js').rule;
var mostCommonWordsRule = require('./most-common-words.js').rule;

var messages = [];
var index = 0;

function Slacker(token) {
  var rtm = new RtmClient( token, { dataStore: new MemoryDataStore() });

  this.forceUpdate = function(callback) {
    if (messages.length > 0) {
      runRules(messages, callback);
    }
  }

  this.startListening = function(callback) {
    rtm.start();
    rtm.on(RTM_EVENTS.MESSAGE, function (message) {
      if (ignored(message)) {
        console.log('message ignored');
        return;
      }
      if (index === 100) {
        index = 0;
      }
      messages.splice(index, 0 , message);
      index++;

      runRules(messages, callback);
    });
  }

  function ignored(message) {
    return message.type !== 'message' || message.bot_id || !message.text;
  }

  function runRules(messages, callback) {
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
      biggestSlacker: biggestSlacker(messages),
      mostActiveChannel: mostActiveChannel(messages),
      mostGifs: mostRecentGif(messages),
      mostCommonWord: mostCommonWordsRule(messages),
      mostRecentQuestion: mostRecentQuestionRule(messages)
    });
  }

  function biggestSlacker(messages) {
    var biggestSlacker = biggestSlackerRule(messages);
    var user = rtm.dataStore.getUserById(biggestSlacker.key);
    return { user: user, payload: biggestSlacker };
  }

  function mostActiveChannel(messages) {
    var mostActiveChannel = mostActiveChannelRule(messages);
    var channel = rtm.dataStore.getChannelById(mostActiveChannel.key);
    return channel;
  }

  function mostRecentGif(messages) {
    var mostGifs = mostRecentGifRule(messages);
    if (mostGifs) {
      var user = rtm.dataStore.getUserById(longestMessage.user);
      return {
        user: user,
        payload: mostGifs
      };
    } else {
      return null;
    }
  }
}
