const ClientType = require('./clientType.js')

module.exports = function Router(botLocator) {
    return {
        route: function (query, next) {
            const roomName = query.room;
            const rawClientType = query.clientType;
            const clientType = ClientType.from(rawClientType);

            switch (clientType) {
                case ClientType.BOT:
                    return next();
                case ClientType.HUMAN:
                    const availableBot = botLocator.locateFirstAvailableBotIn(roomName);

                    if (availableBot) {
                        query.room = availableBot;
                        return next();
                    } else {
                        return next(new Error('No bots available'));
                    }

                default:
                    return next(new Error('Unrecognised clientType: ' + rawClientType));
            }
        }
    };
}
