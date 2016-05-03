const contrib = require('./contrib');

let onReady = function() {
  console.log("ready");
  let submit = document.getElementById('form-submit');

  submit.addEventListener('click', function() {
    console.log('submit clicked')
    let path = document.getElementById("full-path").value;
    contrib.check(path, function(result) {
      appendResult(result);
    });
  })
}

function appendResult(result) {
  let text =  `${result.contributors[0].author} is the owner of ${result.directory}`;
  let resultElement = document.createElement('div');
  let html = '<span id="title" style="display:inline-block; width=100px;">' + text + '</span>';
  resultElement.innerHTML = html;
  let root = document.body;
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
