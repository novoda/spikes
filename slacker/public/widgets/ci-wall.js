if (widgets.ciWall) throw "CiWall already exists";

widgets.ciWall = function() {
  return {
    element: document.getElementById('ci-wall'),
    present: function(data) {
      this.element.innerHTML = '<iframe src="' + data.payload + '" class="frame"></iframe>';
    }
  };
}
