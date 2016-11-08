if (widgets.coverage) throw "Coverage already exists";

widgets.coverage = function() {
  return {
    present: function(data, element) {
      element.innerHTML = presentCoverage(data.payload);
    }
  };
}

function presentCoverage(data) {
  return '<div class="content-container active-image">' +
  '<div class="active-container">' +
    '<div class="active-filler"></div>' +
    '<span class="active-channel-header">' + '&#x1f4ca;' + '</span>' +
    '<br>' +
    '<div>' +
      '<span class="active-channel-copy"><b class="active-channel-name">#' + data.project + '</b><i> has ' + data.coverage + '% coverage</i></span>' +
    '</div>' +
  '</div>' +
'</div>';
}
