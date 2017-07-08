var should = require('should');
var io = require('socket.io-client');
var test = require('unit.js');

var socketURL = 'http://0.0.0.0:5000'

var options ={
    transports: ['websocket'],
    'force new connection': true,

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

    it('Should add human to first available bots room.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connect', function(){
            var bot = io.connect(socketURL, botOptions);

            testObserver.on('connected_bot', function(){
                var human = io.connect(socketURL, options);

                testObserver.on('connected_human', function(roomsWithSockets){
                    var expectedSockets = [bot.id, human.id];
                    var actualSockets = roomsWithSockets[bot.id];

                    test.array(actualSockets)
                        .is(expectedSockets);

                    human.disconnect();
                    bot.disconnect();

                    done();
                });
            });
        });
    });

    it('Should remove human from bots room.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connect', function(){
            var bot = io.connect(socketURL, botOptions);

            testObserver.on('connected_bot', function(){
                var human = io.connect(socketURL, options);

                testObserver.on('connected_human', function(){
                    human.disconnect();

                    testObserver.on('disconnected_human', function(roomsWithSockets){
                        var expectedSockets = [bot.id];
                        var actualSockets = roomsWithSockets[bot.id];

                        test.array(actualSockets)
                            .is(expectedSockets);

                        bot.disconnect();
                        testObserver.disconnect();

                        done();
                    });
                });
            });
        });
    });

});
