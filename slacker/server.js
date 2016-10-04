var app = require('express')();
var express = require('express');
var http = require('http').Server(app);
var io = require('socket.io')(http);
const slackToken = process.env.token;

var Dashboard = require('./server/dashboard.js');
var dashboard = new Dashboard(slackToken);

var cache;

app.use("/public", express.static(__dirname + '/public'));

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

http.listen(3002, function(){
  console.log('listening on *:3002');
});

var notifyClient = function(data) {
  cache = data;
  io.emit('message', data);
}

io.sockets.on('connection', function (socket) {
  if (cache) {
    notifyClient(cache);
  }
});

dashboard.start(notifyClient);
