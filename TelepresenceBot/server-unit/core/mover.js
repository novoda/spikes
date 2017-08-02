var debug = require('debug')('mover');

function Mover(clientsAndRooms, emitter) {
    this.clientsAndRooms = clientsAndRooms;
    this.emitter = emitter;

    this.hasNoRooms = function(clientId) {
        return !this.clientsAndRooms[clientId]
    }
}

Mover.prototype.moveIn = function(clientId, direction) {
    if(this.hasNoRooms(clientId)) {
        return;
    }

    var roomsClientIsIn = Object.keys(this.clientsAndRooms[clientId]);

    for(var i = 0; i < roomsClientIsIn.length; i++) {
        this.emitter.to(roomsClientIsIn[i]).emit('direction', direction);
    }
}

module.exports = function(clientsAndRooms, emitter) {
    return new Mover(clientsAndRooms, emitter);
};
