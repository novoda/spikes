module.exports = {
  rule: thanks
}

function thanks(dataStore, messages) {
  var thankYouMessages = findThanksMessages(dataStore, messages);
  if (!thankYouMessages || thankYouMessages.length === 0) {
    return Promise.reject('no thanks message found');
  } else {
    var latestThanksMessage = thankYouMessages[0];
    return Promise.resolve(createPayload(latestThanksMessage));
  }
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
