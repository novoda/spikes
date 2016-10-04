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
  var rating = '&#xfeb68;'.repeat(selection.score);
  return '<div class="content-container active-image">' +
  '<div class="active-container">' +
    '<div class="active-filler"></div>' +
    '<span class="active-channel-copy"><b>' + data.appName + '</b></span><br>' +
    '<span class="active-channel-copy"><b>' + rating + '</b></span><br><br>' +
    '<span class="active-channel-copy">' + selection.text + '</span>' +
  '</div>' +
'</div>';
}
