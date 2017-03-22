var io = require('socket.io').listen(5000);
var ClientType = require("./clientType.js");
var LoggingClient = require("./loggingClient.js");

var room = "London";
var humans = [];
var bots = [];
var testClient = new LoggingClient();

io.use(function(client, next){

    var rawRoom = client.handshake.query.room;
    var rawClientType = client.handshake.query.clientType;
    var clientType = ClientType.from(rawClientType);

    switch(clientType) {
        case ClientType.TEST:
        case ClientType.BOT:
            return next();
        case ClientType.HUMAN:
            var roomRoster = io.sockets.adapter.rooms[rawRoom];

            if(roomRoster != undefined) {
                for (socketId in roomRoster.sockets) {

                    var socketsInBotRoom = io.sockets.adapter.rooms[socketId];

                    if(botNotConnectedToHuman(socketsInBotRoom)) {
                        client.handshake.query.room = socketId;
                        return next();
                    }
                }
            }

            return next(new Error('No bots available'));
        default:
            return next(new Error('Unrecognised clientType: ' + rawClientType));

    }

});

function botNotConnectedToHuman(socketsInBotRoom) {
    return  socketsInBotRoom.length != undefined && socketsInBotRoom.length == 1;
}

io.sockets.on('connection', function (client) {

    var rawRoom = client.handshake.query.room;
    var rawClientType = client.handshake.query.clientType;
    var clientType = ClientType.from(rawClientType);

    switch(clientType) {
        case ClientType.HUMAN:
            humans.push(client.id);
            client.join(rawRoom);
            testClient.emit('connected_human', humans);
            break;
        case ClientType.BOT:
            bots.push(client.id);
            client.join(rawRoom);
            testClient.emit('connected_bot', bots);
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
        testClient.emit('disconnected_human', humans);
        testClient.emit('disconnected_bot', bots);
    });

});
