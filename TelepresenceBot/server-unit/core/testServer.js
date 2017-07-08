var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    io = require('socket.io')(server),
    path = require('path'),
    debug = require('debug')('server'),
    ClientType = require("./clientType.js");

var server = server.listen(4200, function() {
    debug("Express server listening on port %s", 4200);
});

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

    switch(clientType) {
        case ClientType.TEST:
        case ClientType.BOT:
        case ClientType.HUMAN:
            debug(clientType);
            return next();
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
