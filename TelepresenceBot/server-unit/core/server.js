var io = require('socket.io').listen(5000);
var ClientType = require("./clientType.js");
var LoggingClient = require("./loggingClient.js");
var BotLocator = require("./botLocator.js");

var room = "London";
var humans = [];
var bots = [];
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

    switch(clientType) {
        case ClientType.HUMAN:
            humans.push(client.id);
            client.join(roomName);
            testClient.emit('connected_human', humans);
            break;
        case ClientType.BOT:
            bots.push(client.id);
            client.join(roomName);
            testClient.emit('connected_bot', asRoomsWithSocketIds());
            break;
        case ClientType.TEST:
            console.log('switching test client');
            testClient = new LoggingClient(client);
            testClient.emit('connected');
            break;
        default:
            throw 'Unexpected rawClientType: ' + clientType;
    }


    client.on('disconnect', function() {
        humans.splice(humans.indexOf(client.id), 1);
        bots.splice(bots.indexOf(client.id), 1);
        testClient.emit('disconnected_human', asRoomsWithSocketIds());
        testClient.emit('disconnected_bot', asRoomsWithSocketIds());
    });

});

function asRoomsWithSocketIds() {
    var roomNames = Object.keys(io.sockets.adapter.rooms);
    var roomsWithSockets = {};

    for(var i = 0; i < roomNames.length; i++) {
        var room = io.sockets.adapter.rooms[roomNames[i]];
        roomsWithSockets[roomNames[i]] = Object.keys(room.sockets);
    }

    return roomsWithSockets;
}
