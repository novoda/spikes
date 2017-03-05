var should = require('should');
var io = require('socket.io-client');
var test = require('unit.js');

var socketURL = 'http://0.0.0.0:5000'

var options ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human'
};

describe("TelepresenceBot Server: HumanTest ",function() {

    it('Should throw error when attempting to connect as non-human.', function(done){
        var nonHumanOptions ={
            transports: ['websocket'],
            'force new connection': true,
            query: 'clientType=non-human'
        };

        var human = io.connect(socketURL, nonHumanOptions);

        human.on('error', function(){
            human.disconnect();
            done();
        });
    });

    it('Should add human to list of humans on connection.', function(done) {
        var human = io.connect(socketURL, options);

        human.on('connected', function(actualConnectedHumans){
            var expectedConnectedHumans = [human.id];
            test.array(actualConnectedHumans)
                .is(expectedConnectedHumans);

            human.disconnect();
            done();
        });
    });

    it('Should remove human from list of humans on disconnection.', function(done) {
        var testObserver = io.connect(socketURL, options);
        var human = io.connect(socketURL, options);

        human.on('connected', function(){
            human.disconnect();
        });

        testObserver.on('disconnected', function(actualConnectedHumans) {
            var expectedConnectedHumans = [testObserver.id];

            test.array(actualConnectedHumans)
                .is(expectedConnectedHumans);

            testObserver.disconnect();
            done();
        });
    });

});
