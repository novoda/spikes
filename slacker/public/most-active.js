if (things.mostActiveChannel) throw "MostActiveChannel already exists";

things.mostActiveChannel = function() {
  return {
    element: document.getElementById('most-active-channel'),
    present: function(data) {
      this.element.innerHTML = presentMostActive(data.payload);
    }
  };
}

// function old(data) {
//   return '<div class="centered-horiz block">' +
//   '<div>' +
//       '<div class="right semi-circle shadow">' +
//         '<div class="content centered-vert">' +
//           '<h1 class="text title">#' + data.channel.name + '</h1>' +
//           '<p class="text summary">The most active channel 💬</p>' +
//         '</div>' +
//       '</div>' +
//       '<img src="./public/slack.png" class="left old-avatar"></img>' +
//     '</div>' +
//   '</div>';
// }


function presentMostActive(data) {
  return '<div class="content-container">' +
  '<div>' +
    '<span class="active-channel-header">' + '💬' + '</span>' +
    '<br>' +
    '<div>' +
      // '<span>#' + data.channel.name + '</span>' +
      // '<span> is the most active channel' + '</span>' +
    '</div>' +
  '</div>' +
    // '<img class="image" src="' + data.gallery.url + '"></img>' +
'</div>';
}
