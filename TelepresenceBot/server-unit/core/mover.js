function Mover(clientsAndRooms, emitter) {
    this.clientsAndRooms = clientsAndRooms;
    this.emitter = emitter;
}

Mover.prototype.moveIn = function(client, direction) {
    var roomsClientIsIn = clientsAndRooms[client.id];
    for(var i = 0; i < roomsClientIsIn.length; i++) {
        emitter.to(roomsClientIsIn[i]).emit('direction', direction);
    }
}

module.exports = function(clientsAndRooms, emitter) {
    return new Mover(clientsAndRooms, emitter);
};
