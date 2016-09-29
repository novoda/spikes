module.exports = {
  rule: coverage,
  rank: 1
}

const sonarAuth = process.env.SONAR_AUTH;
var httpClient = require('request');

function coverage() {
  return new Promise(getProjects).then(toAllCoverage).then(toRuleResult);
}

function getProjects(resolve, reject) {
  var projectsRequest = {
    url: 'https://sonar.novoda.com/api/projects/index',
    auth: {
      user: sonarAuth,
      pass: '',
    }
  };

  httpClient.get(projectsRequest, function(error, response, body) {
    var jsonBody = JSON.parse(body);
    var results = jsonBody.map(each => {
      return {
        name: each.nm,
        key: each.k
      }
    });
    resolve(results);
  });
}

function toAllCoverage(data) {
  var allCoverage = data.map(each => {
    return toCoverage(each);
  });
  return Promise.all(allCoverage);
}

function toCoverage(data) {
  var result = function(resolve, reject) {
    var coverageRequest = {
      url:  'https://sonar.novoda.com/api/measures/component',
      qs: {
        componentKey: data.key,
        metricKeys: 'coverage'
      },
      auth: {
        user: sonarAuth,
        pass: '',
      }
    };
    httpClient.get(coverageRequest, function(error, response, body) {
      var jsonBody = JSON.parse(body);
      var measures = jsonBody.component.measures;
      if (measures && measures.length > 0) {
        resolve({
          project: data.name,
          coverage: measures[0].value
        });
      } else {
        resolve();
      }
    });
  };
  return new Promise(result);
}

function toRuleResult(data) {
  var filtered = data.filter(each => {
    return each;
  });
  return new Promise(function(resolve, reject) {
    resolve({
      widgetKey: 'coverage',
      payload: filtered
    });
  });
}
