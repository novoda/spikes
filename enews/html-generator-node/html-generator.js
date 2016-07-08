module.exports = {
  generateHtml: generateHtml
};

var moment = require('moment');

function generateHtml(eNews) {
  var formattedTodaysDate = moment().format('YYYY-MM-DD');
  var html = '<h1 style="font-family:serif">#enews ' + formattedTodaysDate + '</h1><br><table style="width:100%" cellspacing="20">';
  eNews.forEach(function(each) {
    var item = '<tr>' +
    '<td><a href="' + each.link + '"><img src=' + each.imageUrl + ' height="150" width="150"/></td><a/>' +
    '<td><p style="font-family:Comic Sans MS">' + '<i>' + each.originalMessage + '</i>' + '<br>' + each.title + '</p>' +
    '<br><p style="font-family:Comic Sans MS">' + each.poster + '</p></td>';
    html+= item;
  });
 return html + '</table><br><br><br>sik news bro';
};
