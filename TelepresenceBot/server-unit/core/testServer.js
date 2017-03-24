var express = require('express');
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io')(server);

app.get('/', function(req, res) {
    res.json({ message: 'Welcome to the TelepresenceBot api' });
});

var server = server.listen(4200, function() {
    console.log("Express server listening on port " + 4200);
});

io.set("log level", 0);

io.sockets.on("connection", function (socket) {
    socket.on("echo", function (msg, callback) {
        callback = callback || function () {};

        socket.emit("echo", msg);

        callback(null, "Done.");
    });
});

exports.server = server;
