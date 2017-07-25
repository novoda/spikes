var debug = require('debug')('router'),
    ClientType = require('./clientType.js')

function Router(botLocator) {
    this.botLocator = botLocator;
}

Router.prototype.route = function(query, next) {
    var roomName = query.room;
    var rawClientType = query.clientType;
    var clientType = ClientType.from(rawClientType);

    switch(clientType) {
        case ClientType.BOT:
            return next();
        case ClientType.HUMAN:
            var availableBot = this.botLocator.locateFirstAvailableBotIn(roomName);

            if(availableBot) {
                query.room = availableBot;
                return next();
            } else {
                return next(new Error('No bots available'));
            }

        default:
            return next(new Error('Unrecognised clientType: ' + rawClientType));
    }
}

module.exports = function(botLocator) {
    return new Router(botLocator);
};


