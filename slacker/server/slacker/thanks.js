module.exports = {
  rule: thanks
}

function thanks(dataStore, messages) {
  var channel = dataStore.getChannelByName('thanks');
  var thanksMessages = messages.filter(each => {
    return (each.channel == channel.id) && (each.text.indexOf('thank') != -1);
  });

  var randomThankYou = thanksMessages.length > 0 ? thanksMessages[0] : null;
  return {
    widgetKey: 'thanks',
    payload: createPayload(dataStore, randomThankYou)
  };
}

function createPayload(dataStore, thankYou) {
  if (thankYou) {
    return {
      user: dataStore.getUserById(thankYou.user),
      thanks: thankYou
    };
  } else {
    return null;
  }
}
