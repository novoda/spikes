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
  } else if (request.get('X-GitHub-Event') === 'pull_request') {
    const additions = request.body.pull_request.additions;
    const deletions = request.body.pull_request.deletions;
    const total = additions + deletions;
    if (total < 300) {
      console.log('Tiny PR 👏');
    } else if (total < 800) {
      console.log('Medium-sized PR 👍')
    } else {
      console.log('Pretty big PR there... can you split it down?');
    }
    response.status(200).end();
  } else {
    response.status(500).end();
  }
});
