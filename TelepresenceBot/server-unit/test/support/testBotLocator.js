var willResultIn = undefined;

function TestBotLocator() {

}

TestBotLocator.prototype.locateFirstAvailableBotIn = function(room) {
    return willResultIn;
}

TestBotLocator.prototype.willResultIn = function(value) {
    willResultIn = value;
}

module.exports = TestBotLocator;
