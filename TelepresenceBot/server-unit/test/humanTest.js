var should = require('should');
var io = require('socket.io-client');
var test = require('unit.js');

var socketURL = 'http://0.0.0.0:5000'

var options ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human&room=London'
};

var botOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=bot&room=London'
};

var testOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=test'
};

describe("TelepresenceBot Server: HumanTest ",function() {

    it('Should throw error when attempting to connect as non-human.', function(done){
        var nonHumanOptions ={
            transports: ['websocket'],
            'force new connection': true,
            query: 'clientType=non-human'
        };

        var human = io.connect(socketURL, nonHumanOptions);

        human.on('error', function(errorMessage){
            test.string(errorMessage)
                .is("Unrecognised clientType: non-human");

            human.disconnect();
            done();
        });
    });

    it('Should refuse connection when a bot is not available', function(done){
        var human = io.connect(socketURL, options);

        human.on('error', function(errorMessage){
            test.string(errorMessage)
                .is("No bots available");

            human.disconnect();
            done();
        });
    });

    it('Should add human to list of humans on connection.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connect', function(){
            var bot = io.connect(socketURL, botOptions);

            testObserver.on('connected_bot', function(){
                var human = io.connect(socketURL, options);

                testObserver.on('connected_human', function(actualConnectedHumans){
                    var expectedConnectedHumans = [human.id];
                    test.array(actualConnectedHumans)
                        .is(expectedConnectedHumans);

                    human.disconnect();
                    bot.disconnect();

                    done();
                });
            });
        });
    });

    it('Should remove human from list of humans on disconnection.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connect', function(){
            var bot = io.connect(socketURL, botOptions);

            testObserver.on('connected_bot', function(){
                var human = io.connect(socketURL, options);

                testObserver.on('connected_human', function(){
                    human.disconnect();

                    testObserver.on('disconnected_human', function(actualConnectedHumans){
                        test.array(actualConnectedHumans)
                            .isEmpty();

                        bot.disconnect();
                        testObserver.disconnect();

                        done();
                    });
                });
            });
        });
    });

});
