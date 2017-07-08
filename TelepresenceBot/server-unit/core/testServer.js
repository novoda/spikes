var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    io = require('socket.io')(server),
    path = require('path'),
    debug = require('debug')('server'),
    ClientType = require("./clientType.js"),
    BotLocator = require("./botLocator.js");

var server = server.listen(4200, function() {
    debug("Express server listening on port %s", 4200);
});

var botLocator = new BotLocator(io.sockets.adapter.rooms);

app.use(express.static(path.join(__dirname, 'public')));

app.get('/', function(req, res) {
    res.sendFile(__dirname + '/html/index.html');
});

app.get('/rooms', function(req, res) {
    res.sendFile(__dirname + '/json/rooms.json');
});

io.use(function(client, next){
    var roomName = client.handshake.query.room;
    var rawClientType = client.handshake.query.clientType;
    var clientType = ClientType.from(rawClientType);
    debug(clientType);

    switch(clientType) {
        case ClientType.TEST:
            var StaticBotLocator = require("../test/support/staticBotLocator.js");
            botLocator = new StaticBotLocator(io.sockets.adapter.rooms);
            return next();
        case ClientType.BOT:
            return next();
        case ClientType.HUMAN:
            var availableBot = botLocator.locateFirstAvailableBotIn(roomName);

            if(availableBot) {
                client.handshake.query.room = availableBot;
                return next();
            } else {
                return next(new Error('No bots available'));
            }

        default:
            return next(new Error('Unrecognised clientType: ' + rawClientType));
    }
});

io.sockets.on("connection", function (socket) {
    debug('a user connected: %s', socket.id);

    socket.on('disconnect', function(){
        debug('a user disconnected: %s', socket.id);
    });
});

exports.server = server;
