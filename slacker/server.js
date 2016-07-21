var app = require('express')();
var express = require('express');
var http = require('http').Server(app);
var io = require('socket.io')(http);

const token = process.env.token;
var SlackerBar = require('./server/slacker');
var slacker = new SlackerBar(token);

app.use("/public", express.static(__dirname + '/public'));

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

http.listen(3001, function(){
  console.log('listening on *:3001');
});

slacker.startListening(function(data) {
  io.emit('message', data);
});
