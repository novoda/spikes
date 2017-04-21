var functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.sizePR = functions.https.onRequest((request, response) => {
  if (request.get('X-GitHub-Event') === 'ping') {
    console.log('Pong!');
    response.status(200).end();
  } else {
    console.log(request.body.pull_request.additions);
    response.status(200).end();
  }
});
