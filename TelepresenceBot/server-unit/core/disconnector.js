function Disconnector(rooms, connectedClients) {
    this.rooms = rooms;
    this.connectedClients = connectedClients;
}

Disconnector.prototype.disconnectRoom = function(roomName) {
    var room = this.rooms[roomName];

    if(!room) {
        return false;
    }

    var clientsInRoom = room.sockets;

    for(var client in clientsInRoom) {
        var connectedClient = this.connectedClients[client];

        if(!connectedClient){
            return false;
        }

        connectedClient.disconnect();
    }
    return true;
}

module.exports = Disconnector;
