module.exports = {
  generateHtml: generateHtml
};

const moment = require('moment');

function generateHtml(eNews) {
  const html = [];
  html.push(title());
  html.push('<br>');
  html.push(items(eNews));
  return html.join('');
};

function title() {
  const formattedTodaysDate = moment().format('YYYY-MM-DD');
  return '<h1 style="font-family:serif">#enews ' + formattedTodaysDate + '</h1>' +
    '<p style="font-family:Comic Sans MS"><i>External news from the past 7 days!</i></p>';
}

function items(eNews) {
  return '<table style="width:100%" cellspacing="20">' +
    eNews.map(asItem).join('') +
  '</table>';
}

function asItem(eNews) {
  if (eNews.imageUrl) {
    return createImageItem(eNews);
  } else {
    return createTextItem(eNews);
  }
}

function createImageItem(each) {
  return '<tr>' +
  `<td><a href="${each.link}"><img src=${each.imageUrl} height="150" width="150" alt="${each.title}"/></td><a/>` +
  `<td><p style="font-family:Comic Sans MS"><i>${each.originalMessage}</i><br>${each.title}</p>` +
  `<br><p style="font-family:Comic Sans MS">${each.poster}</p></td>` +
  '</tr>';
}

function createTextItem(each) {
  return '<tr><td colspan="2">' +
  `<p style="font-family:Comic Sans MS"><i>${each.originalMessage}</i><br>${each.title}</p>` +
  `<br><a style="font-family:Comic Sans MS" href="${each.link}">${each.link}</a>` +
  `<br><p style="font-family:Comic Sans MS">${each.poster}</p>` +
  '</td></tr>';
}
