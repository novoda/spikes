'use strict';

$(function() {

  var dateInput = $('input#date');
  var getSomethingButton = $('input#get-something');
  var webHelper = new WebHelper(config);

  function init() {
    getSomethingButton.on('click', function(event) {
      event.preventDefault();
      getSomething();
    });
  }

  function getSomething() {
    var date = dateInput.val();
    webHelper.log('Read date: "' + date + '"!');
  }

  init();

});
