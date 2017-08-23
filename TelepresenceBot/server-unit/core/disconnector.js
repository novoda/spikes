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

    var thosePresentIn = function (objectToSearch) {
        return function (key) {
            return objectToSearch[key]
        };
    }

    var asConnectedClient = function () {
        return function (key) {
            return connectedClients[key];
        };
    }

    var connectedClientsThatContainProperty = function (property) {
        return function (connectedClient) {
            return connectedClient.hasOwnProperty(property);
        }
    }

    var disconnectClient = function () {
        return function (connectedClient) {
            connectedClient.disconnect();
        }
    }

    return {
        disconnectRoom: function (roomName) {
            Object.keys(rooms)
                .filter(roomsThatMatch(roomName))
                .filter(roomsThatContainProperty('sockets'))
                .map(function (roomKey) {
                    return Object.keys(rooms[roomKey].sockets)
                        .filter(thosePresentIn(connectedClients))
                        .map(asConnectedClient())
                        .filter(connectedClientsThatContainProperty('disconnect'))
                        .every(disconnectClient());
                });
        }
    };
}
