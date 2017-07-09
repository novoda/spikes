var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    io = require('socket.io')(server),
    path = require('path'),
    debug = require('debug')('server'),
    ClientType = require('./clientType.js'),
    BotLocator = require('./botLocator.js'),
    Router = require('./router.js');

var server = server.listen(4200, function() {
    debug("Express server listening on port %s", 4200);
});

var botLocator = new BotLocator(io.sockets.adapter.rooms);
var router = new Router(io.sockets.adapter.rooms, botLocator);

app.use(express.static(path.join(__dirname, 'public')));

app.get('/', function(req, res) {
    res.sendFile(__dirname + '/html/index.html');
});

app.get('/rooms', function(req, res) {
    res.sendFile(__dirname + '/json/rooms.json');
});

io.use(function(client, next){
    return router.route(client, next);
});

io.sockets.on("connection", function (socket) {
    debug('a user connected: %s', socket.id);

    socket.on('disconnect', function(){
        debug('a user disconnected: %s', socket.id);
    });
});

exports.server = server;
