module.exports = Slacker;

var RtmClient = require('@slack/client').RtmClient;
var RTM_EVENTS = require('@slack/client').RTM_EVENTS;
var RTM_CLIENT_EVENTS = require('@slack/client').CLIENT_EVENTS.RTM;
var MemoryDataStore = require('@slack/client').MemoryDataStore;
var slackerRules = require('./slacker-rules');

var messages = [];
var index = 0;

function Slacker(token) {
  var rtm = new RtmClient( token, { dataStore: new MemoryDataStore() });

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
    // most [word] usages
    // common words
    // most used emoticon
    // nice quote - find text with " foo bar "
    // user with most channel spread
    // user most dedicated to a single channel (by character)

    callback({
      biggestSlacker: biggestSlacker(messages),
      mostActiveChannel: mostActiveChannel(messages),
      longestMessage: longestMessage(messages),
      mostGifs: mostGifs(messages),
      mostCommonWord: slackerRules.mostCommonWord(messages)
    });
  }

  function biggestSlacker(messages) {
    var biggestSlacker = slackerRules.biggestSlacker(messages);
    var user = rtm.dataStore.getUserById(biggestSlacker.key);
    return { user: user, payload: biggestSlacker };
  }

  function mostActiveChannel(messages) {
    var mostActiveChannel = slackerRules.mostActiveChannel(messages);
    var channel = rtm.dataStore.getChannelById(mostActiveChannel.key);
    return channel;
  }

  function longestMessage(messages) {
    var longestMessage = slackerRules.longestMessage(messages);
    var user = rtm.dataStore.getUserById(longestMessage.user);
    return { user: user, payload: longestMessage };
  }

  function mostGifs(messages) {
    var mostGifs = slackerRules.mostGifs(messages);
    if (mostGifs) {
    var user = rtm.dataStore.getUserById(longestMessage.user);
    return { user: user, payload: mostGifs };
    } else {
      return null;
    }
  }
}
