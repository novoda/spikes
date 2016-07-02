var NewsFetcher = require('../enews-node/newsFetcher');
var htmlGenerator = require('./htmlGenerator').generateHtml;
var fs = require('fs');

var token = process.env.token;
var newsFetcher = new NewsFetcher(token);

var latestDate = new Date();
var oldestDate = new Date();
oldestDate.setDate(latestDate.getDate() - 7);

// node timestamps are in milliseconds, need to convert to epoch
var latest = latestDate / 1000;
var oldest = oldestDate / 1000;

newsFetcher.getEnews(oldest, latest, function(eNews) {
  var html = htmlGenerator(eNews);
  writeToFile(html);
});

function writeToFile(content) {
  fs.writeFile("./enews.html", content, function(err) {
    if(err) {
      return console.log(err);
    }
  });
}
