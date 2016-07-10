module.exports = EnewsFetcher;

var Slack = require('./slack.js');
var Q = require('q');

function EnewsFetcher(token) {
  const GENERAL_CHANNEL_ID = 'C029J9QTH'

  var slack = new Slack(token);

  this.getLastSevenDays = function(callback) {
    var latest = new Date();
    var oldest = new Date();
    oldest.setDate(latest.getDate() - 7);

    this.getEnews(oldest, latest, callback);
  }

  this.getEnews = function(oldest, latest, callback) {
    // node timestamps are in milliseconds, need to convert to epoch
    var latestEpoch = latest / 1000;
    var oldestEpoch = oldest / 1000;
    var wrap = function(messages) {
      convertToEnews(messages, callback);
    };
    slack.getMessages(GENERAL_CHANNEL_ID, oldestEpoch, latestEpoch, wrap);
  }

  function convertToEnews(messages, callback) {
    var eNewsMessages = messages.filter(isEnewsMessage);
    var usersPromise = getUsersFrom(eNewsMessages);
    Q.all(usersPromise).then(users => {
      var eNews = toEnews(eNewsMessages, users);
      callback(eNews);
    }).done();
  }

  function isEnewsMessage(message) {
    var messageText = message.text;
    return contains(messageText, '#enews') || contains(messageText, '#eNews') || contains(messageText, '#C0YNBKANM');
  }

  function contains(input, check) {
    return input.indexOf(check) > -1;
  }

  function getUsersFrom(messages) {
    var userIds = messages.map(each => {
      return each.user;
    });
    var promises = [];
    userIds.forEach(function(userId) {
      promises.push(getUser(userId));
    });
    return promises;
  }

  function getUser(userId) {
    return Q.promise(function(resolve, reject, notify) {
      var wrap = function(user) {
          resolve(user);
      };
      slack.getUser(userId, wrap);
    });
  }

  function toEnews(messages, users) {
    return messages.map(message => {
        var posterName = users.filter(user => user.id === message.user).map(user => user.real_name)[0];
        var attachment = message.attachments ? message.attachments[0] : '';
        return {
            originalMessage: message.text,
            title: attachment.title || attachment.text || '',
            link: attachment.title_link || attachment.from_url || '',
            poster: posterName,
            imageUrl: attachment.image_url || attachment.thumb_url || ''
        }
    });
  }

}
