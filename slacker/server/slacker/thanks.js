module.exports = {
  rule: thanks
}

function thanks(dataStore, messages) {
  var result = function(resolve, reject) {
    var thankYouMessages = findThanksMessages(dataStore, messages);
    if (!thankYouMessages || thankYouMessages.length === 0) {
      reject('no thanks message found');
    } else {
      var latestThanksMessage = thankYouMessages[0];
      resolve(createPayload(latestThanksMessage));
    }
  }
  return new Promise(result);
}

function findThanksMessages(dataStore, messages) {
  var channel = dataStore.getChannelByName('thanks');
  return messages.filter(each => {
    return (each.channel == channel.id) && (each.text.indexOf('thank') != -1);
  });
}

function createPayload(dataStore, thankYou) {
  return {
    widgetKey: 'thanks',
    payload: {
      user: dataStore.getUserById(thankYou.user),
      thanks: thankYou
    }
  };
}
