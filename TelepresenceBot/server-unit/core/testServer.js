var express = require('express');
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io')(server);
var path = require('path');
var debug = require('debug')('server');

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

io.sockets.on("connection", function (socket) {
    debug('a user connected');

    socket.on('disconnect', function(){
        debug('user disconnected');
    });

    socket.on('chat message', function(message){
        debug('message: %s', message);
    });

    socket.on("echo", function (msg, callback) {
        callback = callback || function () {};

        socket.emit("echo", msg);

        debug("on Connection");

        callback(null, "Done.");
    });
});

exports.server = server;
