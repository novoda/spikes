module.exports = {
  generateHtml: generateHtml
};

var moment = require('moment');

function generateHtml(eNews) {
  var formattedTodaysDate = moment().format('YYYY-MM-DD');
  var html = '<h1 style="font-family:Comic Sans MS">#enews ' + formattedTodaysDate + '</h1>'
  html += '<p style="font-family:Comic Sans MS"><i>External news from the past 7 days!</i></p>';
  html += '<br><table style="width:100%" cellspacing="20">';
  eNews.forEach(function(each) {
    if (each.imageUrl) {
      html += createImageItem(each);
    } else {
      html += createTextItem(each);
    }
  });
 return html + '</table><br><br><br>sik news bro';
};

function createImageItem(each) {
  return '<tr>' +
  '<td><a href="' + each.link + '"><img src=' + each.imageUrl + ' height="150" width="150" alt="' + each.title + '"/></td><a/>' +
  '<td><p style="font-family:Comic Sans MS">' + '<i>' + each.originalMessage + '</i>' + '<br>' + each.title + '</p>' +
  '<br><p style="font-family:Comic Sans MS">' + each.poster + '</p></td>' +
  '</tr>';
}

function createTextItem(each) {
  return '<tr>' +
  '<p style="font-family:Comic Sans MS">' + '<i>' + each.originalMessage + '</i>' + '<br>' + each.title + '</p>' +
  '<br><p style="font-family:Comic Sans MS">' + each.poster + '</p>' +
  '</tr>';
}
