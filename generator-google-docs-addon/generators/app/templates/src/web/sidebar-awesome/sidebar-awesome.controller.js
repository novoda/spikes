'use strict';

$(function() {

  var textInput = $('input#text');
  var getAwesomenessButton = $('input#get-awesomeness');
  var webHelper = new WebHelper(config);

  function init() {
    getAwesomenessButton.on('click', function(event) {
      event.preventDefault();
      getAwesomeness();
    });
  }

  function getAwesomeness() {
    var text = textInput.val();
    webHelper.log('Read text: "' + text + '"!');
  }

  init();

});
