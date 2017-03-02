var should = require('should');
var io = require('socket.io-client');
var test = require('unit.js');

var socketURL = 'http://0.0.0.0:5000'

var options ={
    transports: ['websocket'],
    'force new connection': true
};

describe("TelepresenceBot Server: DummyTest ",function() {

    it('Should add client to list of clients on connection.', function(done) {
        var client = io.connect(socketURL, options);

        client.on('connected', function(actualClients) {
            var expectedClients = [client.id];

            test.array(actualClients)
                .is(expectedClients);

            client.disconnect();
            done();
        })
    });

});
