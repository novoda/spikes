var express = require('express'),
    app = express(),
    httpServer = require('http').createServer(app),
    io = require('socket.io')(httpServer),
    path = require('path'),
    debug = require('debug')('serverCreator'),
    BotLocator = require('./botLocator.js'),
    Router = require('./router.js');

function ServerCreator() {
    var botLocator = new BotLocator(io.sockets.adapter.rooms);
    var router = new Router(io.sockets.adapter.rooms, botLocator);

    ServerCreator.call(this, router);
}

function ServerCreator(router) {
    app.use(express.static(path.join(__dirname, 'public')));

    app.get('/', function(req, res) {
        res.sendFile(__dirname + '/html/index.html');
    });

    app.get('/rooms', function(req, res) {
        res.sendFile(__dirname + '/json/rooms.json');
    });

    io.use(function(client, next) {
        return router.route(client, next);
    });

    io.sockets.on("connection", function (socket) {
        debug('a user connected: %s', socket.id);

        socket.on('disconnect', function(){
            debug('a user disconnected: %s', socket.id);
        });
    });
}

ServerCreator.prototype.create = function() {
    var server = httpServer.listen(4200, function() {
        debug("Express server listening on port %s", 4200);
    });

    return server;
}

module.exports = ServerCreator;


