const config = require('./config.json');
const Subscriber = require('./subscriber.js');

const subscriber = new Subscriber(config.google);

subscriber.add('adam@novoda.com')
  .then(console.log).catch(console.log);
