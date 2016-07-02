module.exports = {
  generateHtml: generateHtml
};

function generateHtml(eNews) {
  var date = /*moment().format('YYYY-MM-DD')*/ '10/10/10';
  var html = '<h1 style="font-family:Comic Sans MS">#enews ' + date + '</h1><br><table style="width:100%" cellspacing="20">';
  eNews.forEach(function(each) {
    var item = '<tr>' +
    '<td><a href="' + each.link + '"><img src=' + each.imageUrl + ' height="150" width="150"/></td><a/>' +
    '<td><p style="font-family:Comic Sans MS">' + '<i>' + each.originalMessage + '</i>' + '<br>' + each.title + '</p>' +
    '<br><p style="font-family:Comic Sans MS">' + each.poster + '</p></td>';
    html+= item;
  });
 return html + '</table><br><br><br>sik news bro';
};
