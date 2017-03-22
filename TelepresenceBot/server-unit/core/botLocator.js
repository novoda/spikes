function BotLocator(rooms) {
    this.rooms = rooms;
}

BotLocator.prototype.locateFirstAvailableBotIn = function(room) {
    var botsInRoom = this.rooms[room];

    if(botsInRoom != undefined) {
        for (socketId in botsInRoom.sockets) {

            var socketsInBotRoom = this.rooms[socketId];

            if(botNotConnectedToHuman(socketsInBotRoom)) {
                return socketId;
            }
        }
    }
    return undefined;
}

function botNotConnectedToHuman(socketsInBotRoom) {
    return  socketsInBotRoom.length != undefined && socketsInBotRoom.length == 1;
}

module.exports = BotLocator;


