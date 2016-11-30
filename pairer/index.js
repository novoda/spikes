const config = require('./config.json');
const Pairer = require('./pairer');

const pairer = new Pairer(config.gitHub);

const PR_NUMBER = process.env.ghprbPullId

pairer.getPairs(PR_NUMBER).then(console.log);
