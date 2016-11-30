const config = require('./config.json');
const Pairer = require('./pairer');

const PR_NUMBER = process.env.ghprbPullId;
const REPO_NAME = process.env.repoName;

const pairer = new Pairer(config);

pairer.updatePairs(REPO_NAME, PR_NUMBER).then(console.log);
