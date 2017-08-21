var express = require('express'),
    app = express(),
    httpServer = require('http').createServer(app),
    io = require('socket.io')(httpServer),
    path = require('path'),
    debug = require('debug')('server'),
    botLocator = require('./botLocator.js')(io.sockets.adapter.rooms),
    router = require('./router.js')(botLocator),
    disconnector = require('./disconnector.js')(io.sockets.adapter.rooms, io.sockets.connected),
    mover = require('./mover.js')(io.sockets.adapter.sids, io),
    observer = require('./observer.js')();

module.exports = function ServerCreator() {
    app.use(express.static(path.join(__dirname, 'public')));

    app.get('/', function (req, res) {
        res.sendFile(__dirname + '/html/index.html');
    });

    app.get('/rooms', function (req, res) {
        res.sendFile(__dirname + '/json/rooms.json');
    });

    io.use(function (client, next) {
        var query = client.handshake.query;
        return router.route(query, next);
    });

    io.sockets.on('connection', function (socket) {
        var roomName = socket.handshake.query.room;

        socket.join(roomName);
        socket.emit('joined_room', roomName);

        debug('A user connected: %s and joined room: %s', socket.id, roomName);

        socket.on('disconnect', function () {
            debug('A user disconnected: %s and left room: %s', socket.id, roomName);
            disconnector.disconnectRoom(socket.id);
            observer.notify('disconnect', socket.id);
        });

        socket.on('move_in', function (direction) {
            debug('Moving user: %s in direction: %s', socket.id, direction);
            mover.moveIn(socket.id, direction);
            observer.notify('move_in', direction)
        });
    });

    return {
        withRouter: function (alternativeRouter) {
            router = alternativeRouter;
            return this;
        },
        withDisconnector: function (alternativeDisconnector) {
            disconnector = alternativeDisconnector;
            return this;
        },
        withObserver: function (alternativeObserver) {
            observer = alternativeObserver;
            return this;
        },
        withMover: function (alternativeMover) {
            mover = alternativeMover;
            return this;
        },
        create: function () {
            var server = httpServer.listen(4200, function () {
                debug('Express server listening on port %s', 4200);
            });

            return server;
        }
    };
}
