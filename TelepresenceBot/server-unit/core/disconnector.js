module.exports = function Disconnector(rooms, connectedClients) {

    const roomsThatMatch = function (toMatch) {
        return function (room) {
            return toMatch == room;
        };
    }

    const roomsThatContainProperty = function (property) {
        return function (roomName) {
            return rooms[roomName].hasOwnProperty(property);
        };
    }

    const thosePresentIn = function (objectToSearch) {
        return function (key) {
            return objectToSearch[key]
        };
    }

    const asConnectedClient = function () {
        return function (key) {
            return connectedClients[key];
        };
    }

    const connectedClientsThatContainProperty = function (property) {
        return function (connectedClient) {
            return connectedClient.hasOwnProperty(property);
        }
    }

    const disconnectClient = function () {
        return function (connectedClient) {
            connectedClient.disconnect();
            return true;
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
