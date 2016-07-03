var EnewsFetcher = require('./enews-fetcher.js')
const token = process.env.token;

var enewsFetcher = new EnewsFetcher(token);

enewsFetcher.getLastSevenDays(function(eNews) {
  console.log(eNews);
});
