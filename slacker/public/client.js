var main = function() {
  var previousThing;
  var previousPayload;
  var socket = io();

  socket.on('message', function(msg) {
    if (!things[msg.thingKey]) {
      console.log(msg.thingKey + ' is unhandled, skipping');
      return;
    }

    var thing = things[msg.thingKey]();
    if (thing === previousThing && previousPayload === msg.payload) {
      return;
    }
    show(thing, msg)
    previousThing = thing;
    previousPayload = msg.payload;
  });

  var fade = function(previous, thing, data) {
    return function() {
      previous.element.innerHTML = '';
      previous.element.className = '';
      thing.present(data);
      thing.element.className = 'animated fadeIn';
    }
  }

  function show(thing, data) {
    if (previousThing) {
      previousThing.element.className = 'animated fadeOut';
      setTimeout(fade(previousThing, thing, data), 1000);
    } else {
      thing.present(data);
      thing.element.className = 'animated fadeIn';
    }
  }
};

main();
