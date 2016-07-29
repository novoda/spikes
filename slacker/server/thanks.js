module.exports = {
  rule: thanks
}

function thanks(channelId, messages) {
  var thanksMessages = messages.filter(each => {
    return (each.channel == channelId && each.text.indexOf('thank') != -1);
  });

  return thanksMessages[0];
}
