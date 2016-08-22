module.exports = {
  rule: stackoverflow,
  rank: 1
}

var request = require('request');
const SO_URL = 'http://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&q=novoda&accepted=False&site=stackoverflow';

function stackoverflow() {
  return new Promise(getQuestions).then(getCardinality);
}

function getQuestions(resolve, reject) {
  let numItems = -1;
  request.get({url: SO_URL, gzip: true}, parseResponse(resolve, reject));
}

function parseResponse(resolve, reject) {
  return function(error, response, body) {
    if (!error && response.statusCode == 200) {
      var jsonBody = JSON.parse(body);
      if (jsonBody.items) {
        numItems = jsonBody.items.length;
      }
    } else {
      reject(error);
    }
    resolve(numItems);
  }
}

function getCardinality(data) {
  return new Promise(function(resolve, reject) {
    resolve({
      widgetKey: 'stackoverflow',
      payload: data
    });
  });
}
