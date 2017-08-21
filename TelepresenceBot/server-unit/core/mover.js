module.exports = function Mover(clientsAndRooms, emitter) {
    return {
        moveIn: function (clientId, direction) {
            var rooms = clientsAndRooms[clientId];

            Object.keys(rooms || {})
                .map(function (room) {
                    emitter.to(room).emit('direction', direction);
                });
        }
    };
}
