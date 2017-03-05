var io = require('socket.io').listen(5000);

var humans = {};
var bots = {};

io.use(function(client, next){
    console.log(Object.keys(bots).length);

    if(client.handshake.query.clientType == "human" && Object.keys(bots).length > 0) {
        return next();
    } else if(client.handshake.query.clientType == "human" && Object.keys(bots).length <= 0) {
        next(new Error('No bots available'));
    } else {
        next(new Error('Unrecognised clientType: ' + client.handshake.query.clientType));
    }
});

io.sockets.on('connection', function (client) {

    console.log('Connecting: ', client.id);
    humans[client.id] = client;
    io.sockets.emit('connected', toKeysArrayFrom(humans));

    client.on('disconnect', function() {
        console.log('disconnecting: ' + client.id);
        delete humans[client.id];
        io.sockets.emit('disconnected', toKeysArrayFrom(humans));
    });

});

function toKeysArrayFrom(objects) {
    return Object.keys(objects);
}
