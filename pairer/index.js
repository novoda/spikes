const config = require('./config.json');
const Pairer = require('./pairer');

const PR_NUMBER = process.env.ghprbPullId;
const REPO_NAME = process.env.repoName;

const pairer = new Pairer(config);

pairer.updatePairs('all-4', 4820)
  .then(console.log)
  .catch(console.log);
