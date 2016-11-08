if (widgets.ciWall) throw "CiWall already exists";

widgets.ciWall = function() {
  return {
    present: function(data, element) {
      element.innerHTML = '<iframe src="' + data.payload + '" class="frame"></iframe>';
    }
  };
}
