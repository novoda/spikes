if (widgets.biggestSlacker) throw "BiggestSlacker already exists";

widgets.biggestSlacker = function() {
  return {
    present: function(data, element) {
      element.innerHTML = presentBiggestSlacker(data.payload);
    }
  };
}

function presentBiggestSlacker(data) {
  return '<div class="centered-horiz block">' +
  '<div>' +
      '<div class="right semi-circle shadow">' +
        '<div class="content centered-vert">' +
          '<h1 class="text title">@' + data.user.name + '</h1>' +
          '<p class="text summary">Is the biggest Slacker! 😎</p>' +
        '</div>' +
      '</div>' +
      '<img src="'+ data.user.profile.image_512 + '" class="left old-avatar"></img>' +
    '</div>' +
  '</div>';
}
