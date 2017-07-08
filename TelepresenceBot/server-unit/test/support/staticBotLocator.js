function StaticBotLocator(rooms) {
    this.rooms = rooms;
}

StaticBotLocator.prototype.locateFirstAvailableBotIn = function(room) {
    return "dummy_id_for_bot";
}

module.exports = StaticBotLocator;
