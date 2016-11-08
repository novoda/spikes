const app = require('express')();
const express = require('express');
const http = require('http').Server(app);
const io = require('socket.io')(http);
const CONFIG = require('./config.json');
const Dashboard = require('./server/dashboard.js');
const dashboard = new Dashboard(CONFIG.widgets);

let cache;

app.use("/public", express.static(__dirname + '/client/public'));

app.get('/', function(req, res){
  res.sendFile(__dirname + '/client/index.html');
});

http.listen(CONFIG.server.port, function(){
  console.log(`listening on *:${CONFIG.server.port}`);
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
