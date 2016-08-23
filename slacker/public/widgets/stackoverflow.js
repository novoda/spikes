if (widgets.stackoverflow) throw "Stackoverflow already exists";

widgets.stackoverflow = function() {
  return {
    present: function(data, element) {
      element.innerHTML = presentQuestionCount(data.payload)
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
      '<span class="active-channel-copy">We have ' + linkify(data) + '!</span>' +
    '</div>' +
  '</div>' +
'</div>';
}

function linkify(data) {
    return '<a href="' + data.url + '">' + data.count + ' unanswered questions</a>'
}