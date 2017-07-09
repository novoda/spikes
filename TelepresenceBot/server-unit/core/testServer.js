var ServerCreator = require('./serverCreator'),
    BotLocator = require('./botLocator.js');

var botLocator = new BotLocator(io.sockets.adapter.rooms);
var router = new Router(io.sockets.adapter.rooms, botLocator);
var serverCreator = new ServerCreator(router);
serverCreator.create();
