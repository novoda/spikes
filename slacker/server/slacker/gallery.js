module.exports = {
  rule: gallery
}

function gallery(dataStore, messages) {
  var result = function(resolve, reject) {
    var imageMessages = findImageMessages(dataStore, messages);
    if (!imageMessages || imageMessages.length === 0) {
      reject('no image message found');
    } else {
      var latestImageMessage = imageMessages[0];
      resolve(createPayload(dataStore, latestImageMessage));
    }
  }
  return new Promise(result);
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
