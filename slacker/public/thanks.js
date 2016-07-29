if (things.thanks) throw "Thanks already exists";

things.thanks = function() {
  return {
    element: document.getElementById('thanks'),
    present: function(data) {
      if (data.thanks) {
        this.element.innerHTML = presentThanks(data.thanks);
      }
    }
  };
}

function presentThanks(data) {
  console.log(data);
  return '<div class="centered-horiz block">' +
  '<div>' +
    '<div class="right large-semi-circle shadow">' +
      '<div class="content centered-vert">' +
        '<h1 class="text title">#thanks</h1>' +
        '<p class="text summary"><span class="username">@' + data.user.name + ': </span>' + data.payload.text + '  💙</p>' +
      '</div>' +
    '</div>' +
    '<img src="'+ data.user.profile.image_512 + '" class="left avatar"></img>' +
  '</div>' +
'</div>';
}
