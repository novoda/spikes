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
      '<span class="active-channel-copy">We have ' + linkifyCount(data) + '!</span>' +
      '<p>surl.novoda.com/stackoverflow</p>' +
      '<p>Here\'s one: ' + linkifyQuestion(pickRandomQuestion(data)) + '</p>' +
    '</div>' +
  '</div>' +
'</div>';
}

function linkifyCount(data) {
  return '<a style="color: black; text-decoration:none; cursor:default" href="' + data.url + '">' + data.questions.length + ' unanswered questions</a>'
}

function linkifyQuestion(question) {
 return '<a style="color: black; text-decoration:none; cursor:default" href="' + question.link + '">' + question.title + '</a>' 
}

function pickRandomQuestion(data) {
  return data.questions[Math.floor(Math.random() * (data.questions.length))];
}