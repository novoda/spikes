module.exports = {
  rule: mostRecentQuestion
}

var helper = require('./message-helper.js');

function mostRecentQuestion(messages) {
    var timeSortedMessages = messages.sort(helper.sortByTimestamp);
    var questionMessages = timeSortedMessages.filter(function(message) {
      return message.text.indexOf('?') !== -1;
    });
    return questionMessages.length > 0 ? questionMessages[0] : null;
}
