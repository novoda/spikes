if (widgets.thanks) throw "Thanks already exists";

widgets.thanks = function() {
  return {
    present: function(data, element) {
      if (data.payload) {
        element.innerHTML = presentThanks(data.payload);
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
        '<p class="text summary"><span class="username">@' + data.user.name + ': </span>' + data.thanks.text + '  💙</p>' +
      '</div>' +
    '</div>' +
    '<img src="'+ data.user.profile.image_512 + '" class="left old-avatar"></img>' +
  '</div>' +
'</div>';
}
