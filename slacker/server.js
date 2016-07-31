var app = require('express')();
var express = require('express');
var http = require('http').Server(app);
var io = require('socket.io')(http);

const token = process.env.token;
var Slacker = require('./server/slacker.js');
var slacker = new Slacker(token);

app.use("/public", express.static(__dirname + '/public'));

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

http.listen(3001, function(){
  console.log('listening on *:3001');
});

var notifyClient = function(data) {
  io.emit('message', data);
}

io.sockets.on('connection', function (socket) {
  slacker.get(notifyClient);
});

var updateLoop = function() {
  slacker.moveToNext();
  slacker.getCurrentStat(notifyClient);
  setTimeout(updateLoop, 1000 * 5);
}

setTimeout(updateLoop, 1000 * 5);
