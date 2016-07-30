if (things.mostActiveChannel) throw "MostActiveChannel already exists";

things.mostActiveChannel = function() {
  return {
    element: document.getElementById('most-active-channel'),
    present: function(data) {
      this.element.innerHTML = presentMostActive(data.payload.channel);
    }
  };
}

function presentMostActive(data) {
  return '<div class="centered-horiz block">' +
  '<div>' +
      '<div class="right semi-circle shadow">' +
        '<div class="content centered-vert">' +
          '<h1 class="text title">#' + data.name + '</h1>' +
          '<p class="text summary">The most active channel 💬</p>' +
        '</div>' +
      '</div>' +
      '<img src="./public/slack.png" class="left avatar"></img>' +
    '</div>' +
  '</div>';
}
