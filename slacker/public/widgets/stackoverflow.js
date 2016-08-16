if (widgets.stackoverflow) throw "Stackoverflow already exists";

widgets.stackoverflow = function() {
  return {
    element: document.getElementById('stackoverflow'),
    present: function(data) {
      console.log('stackoverflow loaded...');
      this.element.innerHTML = '<h1>Hello' + data.payload + '</h1>'
    }
  };
}