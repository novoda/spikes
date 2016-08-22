module.exports = {
  rule: stackoverflow,
  rank: 1
}

var request = require('request');
const SO_URL = 'http://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&q=novoda&accepted=False&site=stackoverflow';
//const SO_URL = 'http://jsonplaceholder.typicode.com/posts/1';

function stackoverflow() {
  return new Promise(getQuestions).then(toNumber);
}

function getQuestions(resolve, reject) {
  let numItems = -1;
  request.get(
              {
                url: SO_URL,
                method: 'GET',
                gzip: true,
                headers: {
                  'Content-Type': 'application/json'
                }
              }, 
              function(error, response, body) {
                if (!error && response.statusCode == 200) {
                  var jsonBody = JSON.parse(body);
                  if (jsonBody.items) {
                    numItems = jsonBody.items.length;
                  }
                } else {
                  reject(error);
                }
                resolve(numItems);
              });
}

function toNumber(data) {
  return new Promise(function(resolve, reject) {
    resolve({
      widgetKey: 'stackoverflow',
      payload: data
    });
  });
}
