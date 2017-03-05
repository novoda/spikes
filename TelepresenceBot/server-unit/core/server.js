var io = require('socket.io').listen(5000);
var ClientType = require("./clientType.js");
var LoggingClient = require("./loggingClient.js");

var humans = {};
var bots = {};
var testClient = new LoggingClient();

io.use(function(client, next){
    var rawClientType = client.handshake.query.clientType;
    var clientType = ClientType.from(rawClientType);

    switch(clientType) {
        case ClientType.TEST:
        case ClientType.BOT:
            return next();
        case ClientType.HUMAN:
            if(Object.keys(bots).length > 0) {
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
            humans[client.id] = client;
            testClient.emit('connected_human', toKeysArrayFrom(humans));
            break;
        case ClientType.BOT:
            bots[client.id] = client;
            testClient.emit('connected_bot', toKeysArrayFrom(bots));
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
        delete humans[client.id];
        delete bots[client.id];
        testClient.emit('disconnected_human', toKeysArrayFrom(humans));
        testClient.emit('disconnected_bot', toKeysArrayFrom(bots));
    });

});

function toKeysArrayFrom(objects) {
    return Object.keys(objects);
}
