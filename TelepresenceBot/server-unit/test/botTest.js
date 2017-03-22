var should = require('should');
var io = require('socket.io-client');
var test = require('unit.js');

var socketURL = 'http://0.0.0.0:5000'

var options ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=bot&room=London'
};

var humanOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human&room=London'
};

var testOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=test'
};

describe("TelepresenceBot Server: BotTest ",function() {

    it('Should add bot to Room:London on connection.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connected', function(){
            var bot = io.connect(socketURL, options);

            testObserver.on('connected_bot', function(roomsWithSockets){
                var expectedSockets = [bot.id];
                var actualSockets = roomsWithSockets["London"];

                test.array(actualSockets)
                    .is(expectedSockets);

                bot.disconnect();
                testObserver.disconnect();
                done();
            });
        });
    });

    it('Should remove bot from Room:London on disconnection.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connected', function(){
            var bot = io.connect(socketURL, options);

            testObserver.on('connected_bot', function(){
                bot.disconnect();

                testObserver.on('disconnected_bot', function(roomsWithSockets) {
                    var actualSockets = roomsWithSockets["London"];

                    test.value(actualSockets)
                        .isUndefined();

                    testObserver.disconnect();
                    done();
                });
            });
        });
    });

    it('Should disconnect human on bot disconnection.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connected', function(){
            var bot = io.connect(socketURL, options);

            testObserver.on('connected_bot', function(){
                var human = io.connect(socketURL, humanOptions);

                testObserver.on('connected_human', function(){
                    bot.disconnect();

                    testObserver.on('disconnected_human', function(roomsWithSockets){
                        var actualSockets = roomsWithSockets[bot.id];

                        test.value(actualSockets)
                            .isUndefined();

                        testObserver.disconnect();
                        done();
                    });
                });
            });
        });
    });

    it('Should forward movement directions from human to bot.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connected', function(){
            var bot = io.connect(socketURL, options);

            testObserver.on('connected_bot', function(){
                var human = io.connect(socketURL, humanOptions);

                testObserver.on('connected_human', function(){
                    human.emit('move_in', 'w');

                    bot.on('direction', function(actualDirection){
                        test.string(actualDirection)
                            .is('w');

                        testObserver.disconnect();
                        human.disconnect();
                        bot.disconnect();
                        done();
                    });
                });
            });
        });
    });

});
