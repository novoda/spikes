if (widgets.reviews) throw "Reviews already exists";

widgets.reviews = function() {
  return {
    present: function(data, element) {
      console.log(data);
      element.innerHTML = presentReviewsAverage(data.payload);
    }
  };
}

function presentReviewsAverage(data) {
  var selection = data.motivators[Math.floor((Math.random() * data.motivators.length))];
  var rating = '&#11088;'.repeat(selection.score);
  return '<div class="content-container active-image">' +
  '<div class="active-container">' +
    '<div class="active-filler"></div>' +
    '<span class="active-channel-copy review"><b>' + data.appName + '</b><br><br>' +
    '<b>' + rating + '</b><br><br>' +
    '' + selection.text + '</span>' +
  '</div>' +
'</div>';
}
