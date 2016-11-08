var main = function() {
  var previousWidget;
  var previousPayload;
  var socket = io();
  var element = document.getElementById('element');

  socket.on('message', function(msg) {
    if (!widgets[msg.widgetKey]) {
      console.log(msg.widgetKey + ' is unhandled, skipping');
      return;
    }

    var widget = widgets[msg.widgetKey]();
    if (widget === previousWidget && previousPayload === msg.payload) {
      return;
    }
    show(widget, msg)
    previousWidget = widget;
    previousPayload = msg.payload;
  });

  var fade = function(previous, widget, data) {
    return function() {
      element.innerHTML = '';
      element.className = '';
      widget.present(data, element);
      element.className = 'animated fadeIn';
    }
  }

  function show(widget, data) {
    if (previousWidget) {
      element.className = 'animated fadeOut';
      setTimeout(fade(previousWidget, widget, data), 1000);
    } else {
      widget.present(data, element);
      element.className = 'animated fadeIn';
    }
  }
};

main();
