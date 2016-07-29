if (things.mostRecentGif) throw "mostRecentGif already exists";

things.mostActive = function() {
  return {
    element: document.getElementById('most-recent-gif'),
    present: function(data) {
      // gifs are not always present, maybe handle some default state?
      if (data.mostRecentGif) {
        this.element.innerHTML = presentMostRecentGif(data.mostRecentGif);
      }
    }
  };
}

function presentMostRecentGif(data) {
  return '<div class="centered-horiz block">' +
  '<div>' +
    '<img class="right" src="' + data.payload.gif + '"></img>' +
    '<img class="left avatar" src="'+ data.user.profile.image_512 + '"></img>' +
  '</div>';
}
