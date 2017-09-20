module.exports = function Mover(clientsAndRooms, emitter) {

    const emitToRoom = function (direction) {
        return function (room) {
            emitter.to(room).emit('direction', direction);
            return true;
        }
    }

    return {
        moveIn: function (clientId, direction) {
            const rooms = clientsAndRooms[clientId];

            Object.keys(rooms || {})
                .every(emitToRoom(direction));
        }
    };
}
