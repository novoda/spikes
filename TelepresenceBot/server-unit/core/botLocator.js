module.exports = function (locationRooms) {

    const roomsThatMatch = function (toMatch) {
        return function (room) {
            return toMatch == room;
        };
    }

    const roomsThatContainProperty = function (property) {
        return function (roomName) {
            return locationRooms[roomName].hasOwnProperty(property);
        };
    }

    const roomsThatAreNotAlreadyConnectedToOtherSockets = function () {
        return function (roomName) {
            return locationRooms[roomName].length == 1;
        };
    }

    const asEmptyBotRoom = function () {
        return function (locationRoom) {
            return Object.keys(locationRooms[locationRoom].sockets)
                .filter(roomsThatContainProperty('length'))
                .filter(roomsThatAreNotAlreadyConnectedToOtherSockets())
                .pop();
        };
    }

    return {
        locateFirstAvailableBotIn: function (locationRoom) {
            return Object.keys(locationRooms)
                .filter(roomsThatMatch(locationRoom))
                .filter(roomsThatContainProperty('sockets'))
                .map(asEmptyBotRoom())
                .pop();
        }
    };
};
