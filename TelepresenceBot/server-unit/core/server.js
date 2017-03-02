var io = require('socket.io').listen(5000);

var clients = {};

io.sockets.on('connection', function (client) {

    console.log('connecting: ' + client.id);
    clients[client.id] = client;
    io.sockets.emit('connected', toKeysArrayFrom(clients));


    client.on('disconnect', function() {
        console.log('disconnecting: ' + client.id);
        delete clients[client.id];
        io.sockets.emit('disconnected', toKeysArrayFrom(clients));
    });

});

function toKeysArrayFrom(objects) {
    return Object.keys(objects).map(function(key) {
        return key;
    });
}
