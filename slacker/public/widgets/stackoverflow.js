if (widgets.stackoverflow) throw "Stackoverflow already exists";

widgets.stackoverflow = function() {
  return {
    present: function(data, element) {
      element.innerHTML = '<h1>Hello' + data.payload + '</h1>'
    }
  };
}

function presentQuestionCount(data) {
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
