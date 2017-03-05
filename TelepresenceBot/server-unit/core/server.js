var io = require('socket.io').listen(5000);

var humans = {};
var bots = {};

io.use(function(client, next){
    var clientType = client.handshake.query.clientType;

    if(clientType == "bot") {
        return next();
    }

    if(clientType == "human" && Object.keys(bots).length > 0) {
        return next();
    } else if(clientType == "human" && Object.keys(bots).length <= 0) {
        next(new Error('No bots available'));
    } else {
        next(new Error('Unrecognised clientType: ' + clientType));
    }
});

io.sockets.on('connection', function (client) {

    console.log('Connecting: ', client.id);

    switch(client.handshake.query.clientType) {
        case "human":
            humans[client.id] = client;
            io.sockets.emit('connected', toKeysArrayFrom(humans));
            break;
        case "bot":
            bots[client.id] = client;
            io.sockets.emit('connected', toKeysArrayFrom(bots));
            break;
        case "test":
            io.sockets.emit('connected');
            break;
        default:
            throw 'Unexpected clientType: ' + clientType;
    }


    client.on('disconnect', function() {
        console.log('disconnecting: ' + client.id);
        delete humans[client.id];
        delete bots[client.id];
        io.sockets.emit('disconnected', toKeysArrayFrom(humans));
        io.sockets.emit('disconnected_bots', toKeysArrayFrom(humans));
    });

});

function toKeysArrayFrom(objects) {
    return Object.keys(objects);
}
