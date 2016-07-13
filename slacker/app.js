const token = process.env.token;
var Slack = require('./slack.js');
var slack = new Slack(token);
const GENERAL_CHANNEL_ID = 'C029J9QTH'

var latest = new Date();
var oldest = new Date();
oldest.setMinutes(latest.getMinutes() - 30);

var latestEpoch = latest / 1000;
var oldestEpoch = oldest / 1000;

slack.getMessages(GENERAL_CHANNEL_ID, oldestEpoch, latestEpoch, messages => {
  console.log(messages);
});
