const token = process.env.token;
var Slack = require('./slack.js');
var RtmClient = require('@slack/client').RtmClient;
var RTM_EVENTS = require('@slack/client').RTM_EVENTS;
var RTM_CLIENT_EVENTS = require('@slack/client').CLIENT_EVENTS.RTM;
var slack = new Slack(token);
const GENERAL_CHANNEL_ID = 'C029J9QTH';
const LONDON_CHANNEL_ID = 'C096DG4EN';
const BERLIN_CHANNEL_ID = 'C029K2NUY';
const BARCELONA_CHANNEL_ID = 'C096CQ5QW';
const LIVERPOOL_CHANNEL_ID = 'C054WA0TJ';
const TEST_CHANNEL_ID = 'C09H0TJ9Y';

var latest = new Date();
var oldest = new Date();
oldest.setMinutes(latest.getMinutes() - 1000);

// getBiggestSlacker(oldest, latest, result => {
//   console.log(result);
// });


var rtm = new RtmClient(token, {logLevel: 'debug'});
rtm.start();

var messages = [];
var index = 0;

rtm.on(RTM_CLIENT_EVENTS.RTM_CONNECTION_OPENED, function () {
  test();
});

rtm.on(RTM_EVENTS.MESSAGE, function (message) {
  console.log(message);

  if (message.type === 'message' && !message.bot_id) {
    if (index === 100) {
      index = 0;
    }
    messages.splice(index, 0 , message);
    index++;
  }

  // Listens to all `message` events from the team
});

function test() {
  setTimeout(test, 30 * 60);

  var allMessages = flatten(messages);

  console.log('-------------------------------------------------\n' + JSON.stringify(allMessages) + '\n--------------------------------------');

  allMessages.sort((a, b) => {
    return b.messages.length - a.messages.length;
  });

  if (!allMessages || allMessages.length === 0) {
    return;
  }

  var options = {
    as_user: false
  }

  rtm.updateMessage({ text: 'Commented the most : ' + allMessages[0].user, options}, TEST_CHANNEL_ID);
}



// slack.getChannels(result => {
//   console.log(result);
// });


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
