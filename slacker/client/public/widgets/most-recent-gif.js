if (widgets.mostRecentGif) throw "mostRecentGif already exists";

widgets.mostRecentGif = function() {
  return {
    present: function(data, element) {
      element.innerHTML = presentMostRecentGif(data.payload);
    }
  };
}

function presentMostRecentGif(data) {
  return '<div class="centered-horiz block">' +
  '<div>' +
    '<img class="right" src="' + data.mostRecentGif.gif + '"></img>' +
    '<img class="left old-avatar" src="'+ data.user.profile.image_512 + '"></img>' +
  '</div>';
}
