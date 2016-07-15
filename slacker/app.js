const token = process.env.token;
var Slack = require('./slack.js');
var slack = new Slack(token);
const GENERAL_CHANNEL_ID = 'C029J9QTH'

var latest = new Date();
var oldest = new Date();
oldest.setMinutes(latest.getMinutes() - 1000);

getBiggestSlacker(oldest, latest, result => {
  console.log(result);
});

function getBiggestSlacker(oldest, latest, callback) {
  var latestEpoch = latest / 1000;
  var oldestEpoch = oldest / 1000;

  slack.getMessages(GENERAL_CHANNEL_ID, oldestEpoch, latestEpoch, messages => {
    var result = flatten(messages);

    result.sort((a, b) => {
      return b.messages.length - a.messages.length;
    });
    callback(result[0]);
  });
}

function flatten(messages) {
  var userToMessageCount = {};
  messages.forEach(each => {
    if(userToMessageCount[each.user]) {
      userToMessageCount[each.user].push({ text: each.text, timestamp: each.ts} );
    } else {
      userToMessageCount[each.user] = [ {text: each.text, timestamp: each.ts} ];
    }
  });
  return Object.keys(userToMessageCount).map(key => {
      return {
        user: key,
        messages: userToMessageCount[key]
      }
    });
}
