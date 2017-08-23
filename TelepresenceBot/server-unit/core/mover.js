module.exports = function Mover(clientsAndRooms, emitter) {

    var emitToRoom = function (direction) {
        return function (room) {
            emitter.to(room).emit('direction', direction);
        }
    }

    return {
        moveIn: function (clientId, direction) {
            var rooms = clientsAndRooms[clientId];

            Object.keys(rooms || {})
                .every(emitToRoom(direction));
        }
    };
}
