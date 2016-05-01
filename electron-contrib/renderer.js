var contrib = require("./contrib")

var onReady = function() {
  console.log("ready");
  var submit = document.getElementById("form-submit");

  submit.addEventListener('click', function() {
    console.log('submit clicked')
    var path = document.getElementById("full-path").value;
    contrib.check(path, function(result) {
      appendResult(result);
    });
  })
}

function appendResult(result) {
  var text = result.contributors[0].author + " is the owner of " + result.directory;
  var resultElement = document.createElement('div');
  var html = '<span id="title" style="display:inline-block; width=100px;">' + text + '</span>';
  resultElement.innerHTML = html;
  var root = document.body;
  while (resultElement.children.length > 0) {
    root.appendChild(resultElement.children[0]);
  }
}

waitForWindow(onReady);

function waitForWindow(callback) {
  if (document.readyState === 'interactive' || document.readyState === 'complete') {
      callback();
  } else {
    document.addEventListener('DOMContentLoaded', callback, false);
  }
};
