if (widgets.coverage) throw "Coverage already exists";

widgets.coverage = function() {
  return {
    element: document.getElementById('coverage'),
    present: function(data) {
      this.element.innerHTML = presentCoverage(data.payload[Math.floor((Math.random() * data.payload.length))]);
    }
  };
}

function presentCoverage(data) {
  return '<div class="content-container active-image">' +
  '<div class="active-container">' +
    '<div class="active-filler"></div>' +
    '<span class="active-channel-header">' + '&#128172;' + '</span>' +
    '<br>' +
    '<div>' +
      '<span class="active-channel-copy"><b class="active-channel-name">#' + data.project + '</b><i> has ' + data.coverage + '% coverage</i></span>' +
    '</div>' +
  '</div>' +
'</div>';
}
