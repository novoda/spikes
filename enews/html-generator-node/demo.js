var EnewsFetcher = require('enews-fetcher');
var htmlGenerator = require('./html-generator').generateHtml;
var fs = require('fs');

var token = process.env.token;
var enewsFetcher = new EnewsFetcher(token);

enewsFetcher.getLastSevenDays().then(function(eNews) {
  var html = htmlGenerator(eNews, 'http://1234.com/unsubscribe');
  writeToFile(html);
});

function writeToFile(content) {
  fs.writeFile("./enews.html", content, function(err) {
    if(err) {
      return console.log(err);
    }
  });
}
