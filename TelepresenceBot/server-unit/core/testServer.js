var express = require('express');
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io')(server);

var server = server.listen(4200, function() {
    console.log("Express server listening on port " + 4200);
});

app.get('/', function(req, res) {
    res.sendFile(__dirname + '/html/index.html');
});

io.set("log level", 0);

io.sockets.on("connection", function (socket) {
    console.log('a user connected');

    socket.on('disconnect', function(){
        console.log('user disconnected');
    });

    socket.on('chat message', function(msg){
        console.log('message: ' + msg);
    });

    socket.on("echo", function (msg, callback) {
        callback = callback || function () {};

        socket.emit("echo", msg);

        console.log("on Connection");

        callback(null, "Done.");
    });
});

exports.server = server;
