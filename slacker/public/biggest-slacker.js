if (things.biggestSlacker) throw "BiggestSlacker already exists";

things.biggestSlacker = function() {
  return {
    element: document.getElementById('biggest-slacker'),
    data: function(data) {
      this.element.innerHTML = presentBiggestSlacker(data.biggestSlacker);
    }
  };
}

function presentBiggestSlacker(data) {
  console.log(data);
  return '<div class="centered-horiz block">' +
  '<div>' +
      '<div class="right semi-circle shadow">' +
        '<div class="content centered-vert">' +
          '<h1 class="text title">@' + data.user.name + '</h1>' +
          '<p class="text summary">Is the biggest Slacker! 😎</p>' +
        '</div>' +
      '</div>' +
      '<img src="'+ data.user.profile.image_512 + '" class="left avatar"></img>' +
    '</div>' +
  '</div>';
}
