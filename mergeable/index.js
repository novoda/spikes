const Mergeable = require('./mergeable');
const secrets = require('./secrets');

const mergeable = new Mergeable(secrets);

mergeable.checkMergeability()
  .then(() => {
    // success
  }).catch(error => {
    console.log(error);
    process.exit(1);
  });
