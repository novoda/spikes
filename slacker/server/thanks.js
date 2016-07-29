module.exports = {
  rule: thanks
}

function thanks(channelId, messages) {
  var thanksMessages = messages.filter(each => {
    return each.channel == channelId;
  });

  return thanksMessages[0];
}
