var io = require('socket.io').listen(5000);
var ClientType = require("./clientType.js");
var LoggingClient = require("./loggingClient.js");
var BotLocator = require("./botLocator.js");

var testClient = new LoggingClient();
var botLocator = new BotLocator(io.sockets.adapter.rooms);

io.use(function(client, next){

    var roomName = client.handshake.query.room;
    var rawClientType = client.handshake.query.clientType;
    var clientType = ClientType.from(rawClientType);

    switch(clientType) {
        case ClientType.TEST:
        case ClientType.BOT:
            return next();
        case ClientType.HUMAN:
            var human = client;
            var bot = botLocator.locateFirstAvailableBotIn(roomName);
            if(bot == undefined) {
                return next(new Error('No bots available'));
            } else {
                human.handshake.query.room = bot;
                return next();
            }
        default:
            return next(new Error('Unrecognised clientType: ' + rawClientType));

    }

});

io.sockets.on('connection', function (client) {

    var roomName = client.handshake.query.room;
    var rawClientType = client.handshake.query.clientType;
    var clientType = ClientType.from(rawClientType);

    console.log(clientType, client.id);

    switch(clientType) {
        case ClientType.HUMAN:
            client.join(roomName);
            testClient.emit('connected_human', asRoomsWithSocketIds());
            break;
        case ClientType.BOT:
            client.join(roomName);
            testClient.emit('connected_bot', asRoomsWithSocketIds());
            break;
        case ClientType.TEST:
            console.log('switching test client');
            testClient = new LoggingClient(client);
            testClient.emit('connected', asRoomsWithSocketIds());
            break;
        default:
            throw 'Unexpected rawClientType: ' + clientType;
    }

    client.on('disconnect', function() {
        disconnectRoom(client.id);
        testClient.emit('disconnected_human', asRoomsWithSocketIds());
        testClient.emit('disconnected_bot', asRoomsWithSocketIds());
    });

    client.on('move_in', function(direction) {
        var rooms = Object.keys(io.sockets.adapter.sids[client.id]);
        for(var i = 0; i < rooms.length; i++) {
            io.to(rooms[i]).emit('direction', direction);
            testClient.emit('direction', direction);
        }
    });
});

function disconnectRoom(name) {
    var room = io.sockets.adapter.rooms[name];

    if(room != undefined) {
        var clients = io.sockets.adapter.rooms[name].sockets;

        for(var client in clients) {
            var connectedClient = io.sockets.connected[client];
            connectedClient.disconnect();
        }
    }
}

function asRoomsWithSocketIds() {
    var roomNames = Object.keys(io.sockets.adapter.rooms);
    var roomsWithSockets = {};

    for(var i = 0; i < roomNames.length; i++) {
        var room = io.sockets.adapter.rooms[roomNames[i]];
        roomsWithSockets[roomNames[i]] = Object.keys(room.sockets);
    }

    return roomsWithSockets;
}
