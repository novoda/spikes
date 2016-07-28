module.exports = {
  rule: mostActiveChannel
}

function mostActiveChannel(messages) {
  var timeSortedMessages = messages.sort(sortMessagesByTimestamp);
  var allMessages = flattenToChannel(timeSortedMessages);
  allMessages.sort(sortByMessagesLength);
  return allMessages[0];
}

var sortMessagesByTimestamp = (a, b) => {
  return b.ts - a.ts;
}

var sortByMessagesLength = (a, b) => {
  return b.messages.length - a.messages.length;
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
