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
  return '<div class="content-container active-image">' +
  '<div class="active-container">' +
    '<div class="active-filler"></div>' +
    '<span class="active-channel-copy">' + data.demotivators[Math.floor((Math.random() * data.demotivators.length))].text + '</span>' +
  '</div>' +
'</div>';
}
