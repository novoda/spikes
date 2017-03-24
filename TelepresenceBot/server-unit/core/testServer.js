var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);

var port = process.env.PORT || 8080;

var router = express.Router();

router.get('/', function(req, res) {
    res.json({ message: 'Welcome to the TelepresenceBot api' });
});

router.route('/rooms').get(function(req, res) {
    res.sendFile(__dirname + '/json/rooms.json');
});

router.route('/connect').get(function(req, res) {
    var socket = io.listen(http);
    res.send("listening");
});

io.on('connection', function(socket){
  console.log('a user connected');
});

app.use('/api', router);

app.listen(port);
console.log('Starting server on ' + port);
