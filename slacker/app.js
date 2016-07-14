const token = process.env.token;
var Slack = require('./slack.js');
var slack = new Slack(token);
const GENERAL_CHANNEL_ID = 'C029J9QTH'

var latest = new Date();
var oldest = new Date();
oldest.setMinutes(latest.getMinutes() - 1000);

var latestEpoch = latest / 1000;
var oldestEpoch = oldest / 1000;


var foo = [
  { user: 'adam', text: 'one'},
  { user: 'adam', text: 'two'},
  { user: 'carl', text: 'one'},
  { user: 'adam', text: 'three'},
  { user: 'carl', text: 'two'}
];

//
// slack.getMessages(GENERAL_CHANNEL_ID, oldestEpoch, latestEpoch, messages => {
//   var result = parse(messages);
//
//   console.log(result);
//
// });
//
// function parse(messages) {
//   var mapped = messages.map(each => {
//     return {
//       user: each.user,
//       text: each.text,
//       timestamp: each.ts
//     }
//   });
//   return mapped;
// }
