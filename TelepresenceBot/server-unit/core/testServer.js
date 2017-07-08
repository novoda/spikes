var express = require('express');
var http = require('http');
var io = require('socket.io')(server);
var path = require('path');
var debug = require('debug')('server');

var app = express();
var server = http.createServer(app);

var server = server.listen(4200, function() {
    debug("Express server listening on port " + 4200);
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
        debug('message: ', message);
    });

    socket.on("echo", function (msg, callback) {
        callback = callback || function () {};

        socket.emit("echo", msg);

        debug("on Connection");

        callback(null, "Done.");
    });
});

exports.server = server;
