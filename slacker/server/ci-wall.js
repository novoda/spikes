module.exports = {
  rule: ciWall,
  name: 'ciWall'
}

function ciWall() {
  return {
    thingKey: 'ciWall',
    payload: 'https://ci.novoda.com//plugin/jenkinswalldisplay/walldisplay.html?viewName=Active&jenkinsUrl=https%3A%2F%2Fci.novoda.com%2F'
  };
}
