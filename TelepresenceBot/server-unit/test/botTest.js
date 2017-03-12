var should = require('should');
var io = require('socket.io-client');
var test = require('unit.js');

var socketURL = 'http://0.0.0.0:5000'

var options ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=bot'
};

var humanOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human'
};

var testOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=test'
};

describe("TelepresenceBot Server: BotTest ",function() {

    it('Should add bot to list of bots on connection.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connected', function(){
            var bot = io.connect(socketURL, options);

            testObserver.on('connected_bot', function(actualConnectedBots){
                var expectedConnectedBots = [bot.id];
                test.array(actualConnectedBots)
                    .is(expectedConnectedBots);

                bot.disconnect();
                testObserver.disconnect();
                done();
            });
        });
    });

    it('Should remove bot from list of bots on disconnection.', function(done) {
        var testObserver = io.connect(socketURL, testOptions);

        testObserver.on('connected', function(){
            var bot = io.connect(socketURL, options);

            testObserver.on('connected_bot', function(){
                bot.disconnect();

                testObserver.on('disconnected_bot', function(actualConnectedBots) {
                    test.array(actualConnectedBots)
                    .isEmpty();

                    testObserver.disconnect();
                    done();
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
