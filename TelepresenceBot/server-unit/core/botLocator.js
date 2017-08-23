var debug = require('debug')('test');

module.exports = function (rooms) {
    var botNotConnectedToHuman = function (socketsInBotRoom) {
        return socketsInBotRoom.length === 1;
    };

    return {
        locateFirstAvailableBotIn: function (roomToFind) {
            return Object.keys(rooms)
                .filter(function (roomName) {
                    // Get 'London' room. These rooms can only contain bots.
                    debug("roomName == roomToFind: ", roomName, roomName == roomToFind);
                    return roomName == roomToFind;
                })
                .filter(function (roomName) {
                    // Check that it contains 'sockets' property.
                    debug("rooms[roomName].hasOwnProperty('sockets') ", rooms[roomName].hasOwnProperty('sockets'));
                    return rooms[roomName].hasOwnProperty('sockets');
                })
                .map(function (roomName) {
                    // Get the sockets keys.
                    debug("Object.keys(rooms[roomName].sockets): ", Object.keys(rooms[roomName].sockets));
                    return Object.keys(rooms[roomName].sockets)
                        .filter(function (socketKey) {
                            // Iterate over the keys that contain property length.
                            debug("socketKey: ", socketKey);
                            debug("rooms[socketKey].hasOwnProperty('length'): ", rooms[socketKey].hasOwnProperty('length'));
                            return rooms[socketKey].hasOwnProperty('length');
                        })
                        .filter(function (socketKey) {
                            // If length is 1 then not connected to other clients.
                            debug("rooms[socketKey].length == 1: ", rooms[socketKey].length == 1);
                            return rooms[socketKey].length == 1;
                        }).pop();
                }).pop();
        }
    };
};
