var io = require('socket.io').listen(5000);
var ClientType = require("./clientType.js");
var LoggingClient = require("./loggingClient.js");

var room = "London";
var humans = [];
var bots = [];
var testClient = new LoggingClient();

io.use(function(client, next){
    var rawClientType = client.handshake.query.clientType;
    var clientType = ClientType.from(rawClientType);

    switch(clientType) {
        case ClientType.TEST:
        case ClientType.BOT:
            return next();
        case ClientType.HUMAN:
            var roomRoster = io.sockets.adapter.rooms[room];
            if(roomRoster != undefined && roomRoster.length == 1) {
                return next();
            } else {
                return next(new Error('No bots available'));
            }
        default:
            return next(new Error('Unrecognised clientType: ' + rawClientType));

    }
});

io.sockets.on('connection', function (client) {

    var rawClientType = client.handshake.query.clientType;
    var clientType = ClientType.from(rawClientType);

    switch(clientType) {
        case ClientType.HUMAN:
            humans.push(client.id);
            leaveAllRooms(client);
            client.join(room);
            testClient.emit('connected_human', humans);
            break;
        case ClientType.BOT:
            bots.push(client.id);
            leaveAllRooms(client);
            client.join(room);
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
        disconnectRoom(room);

        humans.splice(humans.indexOf(client.id), 1);
        bots.splice(bots.indexOf(client.id), 1);
        testClient.emit('disconnected_human', humans);
        testClient.emit('disconnected_bot', bots);
    });

    client.on('move_in', function(direction) {
        var rooms = Object.keys(io.sockets.adapter.sids[client.id]);
        for(var i = 0; i < rooms.length; i++) {
            io.to(rooms[i]).emit('direction', direction);
            testClient.emit('direction', direction);
        }
    });

    function leaveAllRooms(client) {
        var rooms = Object.keys(io.sockets.adapter.sids[client.id]);
        for(var i = 0; i < rooms.length; i++) {
            client.leave(rooms[i]);
        }
    }

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

});
