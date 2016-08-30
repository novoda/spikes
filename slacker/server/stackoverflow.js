module.exports = {
  rule: stackoverflow,
  rank: 1
}

var request = require('request');
const API_URL = 'http://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&q=novoda&accepted=False&site=stackoverflow';
const SO_URL = 'http://stackoverflow.com/search?q=novoda+hasaccepted%3Ano'

function stackoverflow() {
  return new Promise(getQuestions).then(toRuleResult);
}

function getQuestions(resolve, reject) {
  request.get({url: API_URL, gzip: true}, parseResponse(resolve, reject));
}

function parseResponse(resolve, reject) {
  return function(error, response, body) {
    if (!error && response.statusCode == 200) {
      var jsonBody = JSON.parse(body);
      if (jsonBody.items) {
        resolve({ questions: jsonBody.items, url: SO_URL });
      } 
    } else {
      reject(error);
    }
  }
}

function toRuleResult(data) {
  return new Promise(function(resolve, reject) {
    resolve({
      widgetKey: 'stackoverflow',
      payload: data
    });
  });
}
