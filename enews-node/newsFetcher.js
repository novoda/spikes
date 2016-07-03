module.exports = NewsFetcher;

var Slack = require('./slack.js');
var Q = require('q');

function NewsFetcher(token) {
  const GENERAL_CHANNEL_ID = 'C029J9QTH'

  var slack = new Slack(token);

  this.getEnews = function(oldest, latest, callback) {
    var wrap = function(messages) {
      convertToEnews(messages, callback);
    };
    slack.getMessages(GENERAL_CHANNEL_ID, oldest, latest, wrap);
  }

  function convertToEnews(messages, callback) {
    var eNewsMessages = messages.filter(isEnewsMessage);
    var usersPromise = getUsersFrom(eNewsMessages);
    Q.all(usersPromise).then(users => {
      var eNews = toEnews(eNewsMessages, users);
      callback(eNews);
    });
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
        var attachment = message.attachments[0];
        return {
            originalMessage: message.text,
            title: attachment.title,
            link: attachment.title_link,
            poster: posterName,
            imageUrl: attachment.imageUrl ? attachment.image_url : attachment.thumb_url
        }
    });
  }

}
