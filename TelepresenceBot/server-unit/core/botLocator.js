var debug = require('debug')('test');

module.exports = function (locationRooms) {

    var roomsThatMatch = function (toMatch) {
        return function (room) {
            return toMatch == room;
        };
    }

    var roomsThatContainProperty = function (property) {
        return function (roomName) {
            return locationRooms[roomName].hasOwnProperty(property);
        };
    }

    var roomsThatAreNotAlreadyConnectedToOtherSockets = function () {
        return function (roomName) {
            return locationRooms[roomName].length == 1;
        }
    }

    return {
        locateFirstAvailableBotIn: function (locationRoomToFind) {
            return Object.keys(locationRooms)
                .filter(roomsThatMatch(locationRoomToFind))
                .filter(roomsThatContainProperty('sockets'))
                .map(function (roomName) {
                    return Object.keys(locationRooms[roomName].sockets)
                        .filter(roomsThatContainProperty('length'))
                        .filter(roomsThatAreNotAlreadyConnectedToOtherSockets())
                        .pop();
                }).pop();
        }
    };
};
