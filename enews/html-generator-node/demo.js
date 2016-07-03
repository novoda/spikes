var EnewsFetcher = require('enews-fetcher');
var htmlGenerator = require('./html-generator').generateHtml;
var fs = require('fs');

var token = process.env.token;
var enewsFetcher = new EnewsFetcher(token);

enewsFetcher.getLastSevenDays(function(eNews) {
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
