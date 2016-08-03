module.exports = {
  rule: gallery
}

function gallery(dataStore, messages) {
  var channel = dataStore.getChannelByName('dashboard-images');
  var imageMessages = messages.filter(each => {
    return (each.channel == channel.id) && each.file && (each.file.mimetype.indexOf('image') != -1);
  });

  var imageMessage = imageMessages.length > 0 ? imageMessages[0] : null;
  return {
    thingKey: 'gallery',
    payload: createPayload(dataStore, imageMessage)
  };
}

function createPayload(dataStore, message) {
  if (message) {
    return {
      user: dataStore.getUserById(message.user),
      gallery: {
        url: message.file.url_private
      }
    };
  } else {
    return null;
  }
}
