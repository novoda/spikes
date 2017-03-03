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

        client.on('connect', function(){
            client.emit('connect_as_human', "no_data", assertAddsClientToListOfClients);
        });

        function assertAddsClientToListOfClients(actualConnectedClients) {
            var expectedConnectedClients = [client.id];
            test.array(actualConnectedClients)
                .is(expectedConnectedClients);

            client.disconnect();
            done();
        }
    });

    it('Should remove client from list of clients on disconnection.', function(done) {
        var testObserver = io.connect(socketURL, options);
        var client = io.connect(socketURL, options);

        client.on('connect', function(){
            client.emit('connect_as_human', "no_data", function(){
                client.disconnect();
            });
        });

        testObserver.on('disconnected', function(actualConnectedClients) {
            var expectedConnectedClients = [testObserver.id];

            test.array(actualConnectedClients)
                .isEmpty();

            testObserver.disconnect();
            done();
        });
    });

});
