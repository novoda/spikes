var main = function() {
  var previousWidget;
  var previousPayload;
  var socket = io();

  socket.on('message', function(msg) {
    if (!widgets[msg.thingKey]) {
      console.log(msg.thingKey + ' is unhandled, skipping');
      return;
    }

    var widget = widgets[msg.thingKey]();
    if (widget === previousWidget && previousPayload === msg.payload) {
      return;
    }
    show(widget, msg)
    previousWidget = widget;
    previousPayload = msg.payload;
  });

  var fade = function(previous, widget, data) {
    return function() {
      previous.element.innerHTML = '';
      previous.element.className = '';
      widget.present(data);
      widget.element.className = 'animated fadeIn';
    }
  }

  function show(widget, data) {
    if (previousWidget) {
      previousWidget.element.className = 'animated fadeOut';
      setTimeout(fade(previousWidget, widget, data), 1000);
    } else {
      widget.present(data);
      widget.element.className = 'animated fadeIn';
    }
  }
};

main();
