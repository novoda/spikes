module.exports = {
  rule: stackoverflow
}

var request = require('request');

function fetch() {
  console.log('... stackoverflow FETCH')
  // request('http://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&q=novoda&accepted=False&site=stackoverflow', 
  //         function (error, response, body) {
  //           if (!error && response.statusCode == 200) {
  //               console.log(body.items);
  //               return body.items;
  //           } else {
  //               console.log(error);
  //           }
  //       })
}

function stackoverflow() {
  console.log('... stackoverflow rule');
  questions = fetch();
  return {
    widgetKey: 'stackoverflow',
    payload: '!!!' // TODO number of unanswered questions
  };
}
