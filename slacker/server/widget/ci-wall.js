module.exports = {
  rule: ciWall,
  rank: 1
}

function ciWall() {
  return Promise.resolve({
    widgetKey: 'ciWall',
    payload: 'https://ci.novoda.com//plugin/jenkinswalldisplay/walldisplay.html?viewName=Active&jenkinsUrl=https%3A%2F%2Fci.novoda.com%2F'
  });
}
