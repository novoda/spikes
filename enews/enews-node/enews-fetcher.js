const Slack = require('./slack.js');
const Q = require('q');

function EnewsFetcher(token) {
  const GENERAL_CHANNEL_ID = 'C029J9QTH'
  const slack = new Slack(token);

  this.getLastSevenDays = function(callback) {
    var latest = new Date();
    var oldest = new Date();
    oldest.setDate(latest.getDate() - 7);

    this.getEnews(oldest, latest, callback);
  }

  this.getEnews = function(oldest, latest, callback) {
    // node timestamps are in milliseconds, need to convert to epoch
    const latestEpoch = latest / 1000;
    const oldestEpoch = oldest / 1000;
    const wrap = function(messages) {
      convertToEnews(messages, callback);
    };
    slack.getMessages(GENERAL_CHANNEL_ID, oldestEpoch, latestEpoch, wrap);
  }

  function convertToEnews(messages, callback) {
    const eNewsMessages = messages.filter(isEnewsMessage);
    const usersPromise = getUsersFrom(eNewsMessages);
    Q.all(usersPromise).then(users => {
      const eNews = toEnews(eNewsMessages, users);
      callback(eNews);
    }).done();
  }

  function isEnewsMessage(message) {
    const messageText = message.text;
    return contains(messageText, '#enews') || contains(messageText, '#eNews') || contains(messageText, '#C0YNBKANM');
  }

  function contains(input, check) {
    return input.indexOf(check) > -1;
  }

  function getUsersFrom(messages) {
    const userIds = messages.map(each => {
      return each.user;
    });
    const promises = [];
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
        const posterName = users.filter(user => user.id === message.user).map(user => user.real_name)[0];
        const attachment = message.attachments ? message.attachments[0] : '';
        return {
            originalMessage: sanitiseMessage(message.text),
            title: attachment.title || attachment.text || '',
            link: attachment.title_link || attachment.from_url || findUrlFrom(message.text) || '',
            poster: posterName,
            imageUrl: attachment.image_url || attachment.thumb_url || ''
        }
    });
  }

  function sanitiseMessage(message) {
    return message.replace(/<.*?>/g, '').trim();
  }

  function findUrlFrom(message) {
    const uriPattern = /\b((?:[a-z][\w-]+:(?:\/{1,3}|[a-z0-9%])|www\d{0,3}[.]|[a-z0-9.\-]+[.][a-z]{2,4}\/)(?:[^\s()<>]+|\(([^\s()<>]+|(\([^\s()<>]+\)))*\))+(?:\(([^\s()<>]+|(\([^\s()<>]+\)))*\)|[^\s`!()\[\]{};:'".,<>?«»“”‘’]))/ig;
    const match = message.match(uriPattern)
    return match ? match[0] : undefined;
  }

}

module.exports = EnewsFetcher;
