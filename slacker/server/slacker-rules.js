module.exports = {
  biggestSlacker: biggestSlacker,
  mostActiveChannel: mostActiveChannel,
  longestMessage: longestMessage,
  mostGifs: mostGifs,
  mostCommonWord: mostCommonWord,
  mostRecentQuestion: mostRecentQuestion
}

var biggestSlackerRule = require('./biggest-slacker.js').rule;
var mostActiveChannelRule = require('./most-active-channel.js').rule;

function biggestSlacker(messages) {
  return biggestSlackerRule(messages);
}

function mostGifs(messages) {
  var gifMessages = messages.filter(each => {
    return each.text.indexOf('.gif') !== -1;
  });

  if (gifMessages.length === 0) {
    return null;
  }
  var allMessages = flattenToUser(gifMessages);
  allMessages.sort(sortByMessagesLength);;
  return allMessages[0];
}

function mostActiveChannel(messages) {
  return mostActiveChannelRule(messages);
}

function longestMessage(messages) {
  var allMessages = [].concat(messages);
  allMessages.sort((a, b) => {
      return b.text.length - a.text.length;
  });
  return allMessages[0];
}

function flattenToUser(messages) {
  return flattenMessages(messages, 'user');
}

function flattenToChannel(messages) {
  return flattenMessages(messages, 'channel');
}

function flattenMessages(messages, flattenKey) {
  var dict = {};
  messages.forEach(each => {
    if(dict[each[flattenKey]]) {
      dict[each[flattenKey]].push(each);
    } else {
      dict[each[flattenKey]] = [each];
    }
  });
  return Object.keys(dict).map(key => {
      return {
        key: key,
        messages: dict[key]
      }
    });
}

var sortByMessagesLength = (a, b) => {
  return b.messages.length - a.messages.length;
}

var sortMessagesByTimestamp = (a, b) => {
  return b.ts - a.ts;
}

function mostCommonWord(messages) {
  var words = messages.map(each => {
      return each.text;
  }).reduce((prev, curr) => {
    return prev + ' ' + curr;
  }).replace(',', '').split(' ');

  var frequency = {};  // array of frequency.
  var max = 0;  // holds the max frequency.
  var result;   // holds the max frequency element.
  for(var v in words) {
          frequency[words[v]]=(frequency[words[v]] || 0)+1; // increment frequency.
          if(frequency[words[v]] > max) { // is this frequency > max so far ?
                  max = frequency[words[v]];  // update max.
                  result = words[v];          // update result.
          }
  }
  return {
    word: result,
    count: max
  }
}

function mostRecentQuestion(messages) {
    var timeSortedMessages = messages.sort(sortMessagesByTimestamp);
    var questionMessages = timeSortedMessages.filter(function(message) {
      return message.text.indexOf('?') !== -1;
    });
    return questionMessages.length > 0 ? questionMessages[0] : null;
}
