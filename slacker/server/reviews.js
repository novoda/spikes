module.exports = {
  rule: reviews,
  rank: 1
}

var gplay = require('google-play-scraper');

function reviews() {
  var request = gplay.reviews({
    appId: 'com.channel4.ondemand',
    page: 0,
    sort: gplay.sort.NEWEST
  });
  return request.then(toRuleResult);
}

function toRuleResult(data) {
  var total = 0;
  data.forEach(each => {
    total += each.score;
  })
  var average = total / data.length;
  console.log(total + ' : ' + data.length + ' ' + average);

  var demotivators = data.filter(each => each.score < 2);
  var motivators = data.filter(each => each.score > 2);

  return new Promise(function(resolve, reject) {
    resolve({
      widgetKey: 'reviews',
      payload: {
        average: average,
        demotivators: demotivators,
        motivators: motivators
      }
    });
  });
}
