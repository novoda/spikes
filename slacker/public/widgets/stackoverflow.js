if (widgets.stackoverflow) throw "Stackoverflow already exists";

widgets.stackoverflow = function() {
  return {
    present: function(data, element) {
      element.innerHTML = presentQuestion(data.payload)
    }
  };
}

function presentQuestion(data) {
  return '<div class="content-container active-image">' +
  '<div class="active-container">' +
    '<div class="active-filler"></div>' +
    '<span class="active-channel-header">' + '&#128172;' + '</span>' +
    '<br>' +
    '<div>' +
      '<span class="active-channel-copy">We have ' + linkifyCount(data.questions) + '!</span>' +
      '<p class="">Here\'s one: ' + linkifyQuestion(data.questions[0]) + '</p>' +
    '</div>' +
  '</div>' +
'</div>';
}

function linkifyCount(questions) {
  return '<a href="' + questions.url + '">' + questions.length + ' unanswered questions</a>'
}

function linkifyQuestion(question) {
 return '<a href="' + question.link + '">' + question.title + '</a>' 
}