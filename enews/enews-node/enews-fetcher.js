const Slack = require('./slack.js');
const GENERAL_CHANNEL_ID = 'C029J9QTH'

function EnewsFetcher(token) {
  this.slack = new Slack(token);
}

EnewsFetcher.prototype.getLastSevenDays = function(callback) {
    var latest = new Date();
    var oldest = new Date();
    oldest.setDate(latest.getDate() - 7);

    getEnewsInternal(this.slack, oldest, latest, callback)
  }

EnewsFetcher.prototype.getEnews = function(oldest, latest, callback) {
  getEnewsInternal(this.slack, oldest, latest, callback)
}

function getEnewsInternal(slack, oldest, latest, callback) {
  // node timestamps are in milliseconds, need to convert to epoch
  const latestEpoch = latest / 1000;
  const oldestEpoch = oldest / 1000;
  slack.getMessages(GENERAL_CHANNEL_ID, oldestEpoch, latestEpoch)
    .then(filterNonNews)
    .then(convertToEnews(slack))
    .then(callback);
}

function filterNonNews(messages) {
  return Promise.resolve(messages.filter(message => {
    const text = message.text;
    return text.includes('#enews') || text.includes('#eNews') || text.includes('#C1V389HGB|enews') || text.includes('#C0YNBKANM');
  }));
}

function convertToEnews(slack) {
  return function(messages) {
    return Promise.all(getUsers(slack, messages))
      .then(users => {
        return Promise.resolve(toEnewsModel(messages, users));
    });
  }
}

const getUsers = (slack, messages) => messages.map(each => slack.getUser(each.user) );

function toEnewsModel(messages, users) {
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

module.exports = EnewsFetcher;
