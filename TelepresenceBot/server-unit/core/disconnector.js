module.exports = function Disconnector(rooms, connectedClients) {

    var roomsThatMatch = function (toMatch) {
        return function (room) {
            return toMatch == room;
        };
    }

    var roomsThatContainProperty = function (property) {
        return function (roomName) {
            return rooms[roomName].hasOwnProperty(property);
        };
    }

    return {
        disconnectRoom: function (roomName) {
            Object.keys(rooms)
                .filter(roomsThatMatch(roomName))
                .filter(roomsThatContainProperty('sockets'))
                .map(function (roomKey) {
                    return Object.keys(rooms[roomKey].sockets)
                        .filter(function (key) {
                            return connectedClients[key];
                        })
                        .map(function (key) {
                            return connectedClients[key];
                        })
                        .filter(function (connectedClient) {
                            return connectedClient.hasOwnProperty('disconnect');
                        })
                        .every(function (connectedClient) {
                            connectedClient.disconnect();
                        })
                });
        }
    };
}
