var io = require('socket.io').listen(5000);

var humans = {};

io.use(function(client, next){
    if(client.handshake.query.clientType == "human") {
        return next();
    }
    next(new Error('Unrecognised clientType: ', client.handshake.query.clientType));
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
