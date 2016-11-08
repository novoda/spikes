module.exports = {
  rule: gallery
}

function gallery(dataStore, messages) {
  var imageMessages = findImageMessages(dataStore, messages);
  if (!imageMessages || imageMessages.length === 0) {
    return Promise.reject('no image message found');
  } else {
    var latestImageMessage = imageMessages[0];
    return Promise.resolve(createPayload(dataStore, latestImageMessage));
  }
}

function findImageMessages(dataStore, messages) {
  var channel = dataStore.getChannelByName('dashboard-images');
  return messages.filter(each => {
    return (each.channel == channel.id) && each.file && (each.file.mimetype.indexOf('image') != -1);
  });
}

function createPayload(dataStore, imageMessage) {
  return {
    widgetKey: 'gallery',
    payload: {
      user: dataStore.getUserById(imageMessage.user),
      gallery: {
        url: imageMessage.file.url_private
      }
    }
  };
}
