function StaticBotLocator(shouldLocateBot) {
    this.shouldLocateBot = shouldLocateBot;
}

StaticBotLocator.prototype.locateFirstAvailableBotIn = function(room) {
    if(this.shouldLocateBot) {
        return "dummy_id_for_bot";
    } else {
        return;
    }
}

module.exports = StaticBotLocator;
