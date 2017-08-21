module.exports = function Disconnector(rooms, connectedClients) {
    return {
        disconnectRoom: function (roomName) {
            var room = rooms[roomName];

            if (!room) {
                return false;
            }

            var clientsInRoom = room.sockets;

            for (var client in clientsInRoom) {
                var connectedClient = connectedClients[client];

                if (!connectedClient) {
                    return false;
                }

                connectedClient.disconnect();
            }
            return true;
        }
    };
}
