var ServerCreator = require('./serverCreator'),
    BotLocator = require('./botLocator.js');

var botLocator = new BotLocator(io.sockets.adapter.rooms);
var serverCreator = new ServerCreator(botLocator);
serverCreator.create();
