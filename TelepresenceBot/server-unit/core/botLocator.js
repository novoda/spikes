function BotLocator(rooms) {
    return {
        locateFirstAvailableBotIn: function(room) {
            var botsInRoom = rooms[room];

            if(!botsInRoom) {
                return;
            }

            for (socketId in botsInRoom.sockets) {
                var socketsInBotRoom = rooms[socketId];

                if(botNotConnectedToHuman(socketsInBotRoom)) {
                    return socketId;
                }
            }
        }
    }
}

function botNotConnectedToHuman(socketsInBotRoom) {
    return  socketsInBotRoom.length === 1;
}

module.exports = function(rooms) {
    return new BotLocator(rooms);
};
