module.exports = {
  generateHtml: generateHtml
};

const moment = require('moment');
const Mustache = require('mustache');
const fs = require('fs');

function generateHtml(eNews) {
  const title = fs.readFileSync('view/partial/title.mst', 'utf-8');
  const textItem = fs.readFileSync('view/partial/text-item.mst', 'utf-8');
  const imageItem = fs.readFileSync('view/partial/image-item.mst', 'utf-8');
  const item = fs.readFileSync('view/partial/item.mst', 'utf-8');

  const template = fs.readFileSync('view/enews.mst', 'utf-8');
  const formattedTodaysDate = moment().format('YYYY-MM-DD');

  const model = {
    formattedTodaysDate: formattedTodaysDate,
    eNews: eNews
  };

  const partials = {
    title: title,
    textItem: textItem,
    imageItem: imageItem,
    item: item
  }

  return Mustache.render(template, model, partials);
};
