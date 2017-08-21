module.exports = function (rooms) {
    var botNotConnectedToHuman = function (socketsInBotRoom) {
        return socketsInBotRoom.length === 1;
    };

    return {
        locateFirstAvailableBotIn: function (room) {
            var botsInRoom = rooms[room];

            if (!botsInRoom) {
                return;
            }

            for (socketId in botsInRoom.sockets) {
                var socketsInBotRoom = rooms[socketId];

                if (botNotConnectedToHuman(socketsInBotRoom)) {
                    return socketId;
                }
            }
        }
    };
};
