if (things.mostActiveChannel) throw "MostActiveChannel already exists";

things.mostActiveChannel = function() {
  return {
    element: document.getElementById('most-active-channel'),
    present: function(data) {
      this.element.innerHTML = presentMostActive(data.payload);
    }
  };
}

function presentMostActive(data) {
  return '<div class="content-container">' +
  '<div class="active-container">' +
    '<span class="active-channel-header">' + '💬' + '</span>' +
    '<br>' +
    '<div>' +
      '<span class="active-channel-copy"><b>#' + data.channel.name + '</b></span>' +
      '<span class="active-channel-copy"><i> is the most active channel' + '</i></span>' +
    '</div>' +
  '</div>' +
    // '<img class="image" src="' + data.gallery.url + '"></img>' +
'</div>';
}
