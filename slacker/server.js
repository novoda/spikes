const app = require('express')();
const express = require('express');
const http = require('http').Server(app);
const io = require('socket.io')(http);
const slackToken = process.env.token;

const Dashboard = require('./server/dashboard.js');
const dashboard = new Dashboard(slackToken);

let cache;

app.use("/public", express.static(__dirname + '/public'));

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

http.listen(3002, function(){
  console.log('listening on *:3002');
});

io.sockets.on('connection', function (socket) {
  if (cache) {
    notifyClient(cache);
  }
});

function notifyClient(data) {
  cache = data;
  io.emit('message', data);
}

dashboard.start(notifyClient);
