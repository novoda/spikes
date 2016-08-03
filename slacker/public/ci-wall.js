if (things.ciWall) throw "CiWall already exists";

things.ciWall = function() {
  return {
    element: document.getElementById('ci-wall'),
    present: function(data) {
      this.element.innerHTML = '<iframe src="https://ci.novoda.com//plugin/jenkinswalldisplay/walldisplay.html?viewName=Active&jenkinsUrl=https%3A%2F%2Fci.novoda.com%2F" style="border:none; padding-top: 200px; padding-left: 50px;" height="780" width="1800"></iframe>'
    }
  };
}
