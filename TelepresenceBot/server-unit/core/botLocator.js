var debug = require('debug')('test');

module.exports = function (rooms) {

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

    var roomsThatAreNotAlreadyConnectedToOtherSockets = function () {
        return function (roomName) {
            return rooms[roomName].length == 1;
        }
    }

    return {
        locateFirstAvailableBotIn: function (roomToFind) {
            return Object.keys(rooms)
                .filter(roomsThatMatch(roomToFind))
                .filter(roomsThatContainProperty('sockets'))
                .map(function (roomName) {
                    return Object.keys(rooms[roomName].sockets)
                        .filter(roomsThatContainProperty('length'))
                        .filter(roomsThatAreNotAlreadyConnectedToOtherSockets())
                        .pop();
                }).pop();
        }
    };
};
