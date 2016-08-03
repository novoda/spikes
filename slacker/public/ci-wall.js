if (things.ciWall) throw "CiWall already exists";

things.ciWall = function() {
  return {
    element: document.getElementById('ci-wall'),
    present: function(data) {
      this.element.innerHTML = '<iframe src="' + data.payload + '" class="frame"></iframe>'
    }
  };
}
