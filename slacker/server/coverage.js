module.exports = {
  rule: coverage,
  rank: 1
}

const sonarAuth = process.env.SONAR_AUTH;
const PROJECTS_REQUEST = {
  url: 'https://sonar.novoda.com/api/projects/index',
  auth: {
    user: sonarAuth,
    pass: '',
  }
};
const httpClient = require('request-promise-native');

var currentIndex = 0;

function coverage() {
  return httpClient(PROJECTS_REQUEST)
    .then(parseProjects)
    .then(toAllCoverage)
    .then(toRuleResult);
}

function parseProjects(body) {
  var jsonBody = JSON.parse(body);
  var results = jsonBody.map(each => {
    return {
      name: each.nm,
      key: each.k
    }
  });
  return Promise.resolve(results);
}

function toAllCoverage(data) {
  var allCoverage = data.map(each => {
    var coverageRequest = createCoverageRequest(each.key);
    return httpClient(coverageRequest)
      .then(parseCoverage(each.name));
  });
  return Promise.all(allCoverage);
}

function createCoverageRequest(key) {
  return {
    url:  'https://sonar.novoda.com/api/measures/component',
    qs: {
      componentKey: key,
      metricKeys: 'coverage'
    },
    auth: {
      user: sonarAuth,
      pass: '',
    }
  };
}

function parseCoverage(projectName) {
  return function(body) {
    var jsonBody = JSON.parse(body);
    var measures = jsonBody.component.measures;
    if (measures && measures.length > 0) {
      return Promise.resolve({
        project: projectName,
        coverage: measures[0].value
      });
    } else {
      return Promise.resolve({});
    }
  }
}

function toRuleResult(data) {
  var filtered = data.filter(each => {
    return each.project;
  });

  incrementIndex(filtered.length);

  return Promise.resolve({
    widgetKey: 'coverage',
    payload: filtered[currentIndex]
  });
}

function incrementIndex(dataLength) {
  if (currentIndex >= dataLength) {
    currentIndex = 0;
  } else {
    currentIndex++;
  }
}
