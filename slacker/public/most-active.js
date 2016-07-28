things.mostActive = function() {
  return {
    element: document.getElementById('most-active-channel'),
    data: function(data) {
      this.element.innerHTML = presentMostActive(data.mostActiveChannel);
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
