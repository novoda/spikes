module.exports = {
  rule: stackoverflow,
  rank: 1
}

const SO_URL = 'http://stackoverflow.com/search?q=novoda+hasaccepted%3Ano'
const httpClient = require('request-promise-native');
const REQUEST = {
  url: 'http://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&q=novoda&accepted=False&site=stackoverflow',
  gzip: true
};

function stackoverflow() {
  return getQuestions().then(toRuleResult);
}

function getQuestions() {
  return httpClient(REQUEST).then(parseResponse);
}

function parseResponse(body) {
  var jsonBody = JSON.parse(body);
  if (jsonBody.items) {
    return Promise.resolve({ questions: jsonBody.items, url: SO_URL });
  } else {
    return Promise.reject('stackoverflow: no items');
  }
}

function toRuleResult(data) {
  return Promise.resolve({
    widgetKey: 'stackoverflow',
    payload: data
  });
}
