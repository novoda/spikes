const config = require('./config.json');
const Subscriber = require('./subscriber.js');

const subscriber = new Subscriber(config.google);

subscriber.add('1')
  .then(console.log).catch(console.log);


// subscriber.getAll()
//   .then(console.log).catch(console.log);
