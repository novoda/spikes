function Mover(clientsAndRooms, emitter) {
    var hasNoRooms = function(clientId) {
        return !clientsAndRooms[clientId];
    };

    return {
        moveIn: function(clientId, direction) {
            if(hasNoRooms(clientId)) {
                return;
            }

            var roomsClientIsIn = Object.keys(clientsAndRooms[clientId]);

            for(var i = 0; i < roomsClientIsIn.length; i++) {
                emitter.to(roomsClientIsIn[i]).emit('direction', direction);
            }
        }
    };
}

module.exports = function(clientsAndRooms, emitter) {
    return new Mover(clientsAndRooms, emitter);
};
