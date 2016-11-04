const gplay = require('google-play-scraper');
const APPS = [
  {
    package: 'com.channel4.ondemand',
    name: 'All 4'
  },
  {
    package: 'uk.co.thetimes',
    name: 'The Times'
  },
  {
    package: 'com.soundcloud.creators',
    name: 'Soundcloud Pulse'
  }
];

let currentIndex = 0;

function Reviews() {}

Reviews.prototype.rank = 1;

Reviews.prototype.rule = function() {
  var app = APPS[currentIndex];
  incrementIndex(APPS.length);

  var request = gplay.reviews({
    appId: app.package,
    page: 0,
    sort: gplay.sort.NEWEST
  });
  return request.then(toRuleResult(app));
}

function incrementIndex(dataLength) {
  if (currentIndex >= dataLength - 1) {
    currentIndex = 0;
  } else {
    currentIndex++;
  }
}

function toRuleResult(app) {
  return function(data) {
    var total = 0;
    data.forEach(each => {
      total += each.score;
    });

    var average = total / data.length;
    var demotivators = data.filter(each => each.score < 2);
    var motivators = data.filter(each => each.score > 2);

    return Promise.resolve({
      widgetKey: 'reviews',
      payload: {
        average: average,
        demotivators: demotivators,
        motivators: motivators,
        appName: app.name
      }
    });
  }
}

module.exports = Reviews;
