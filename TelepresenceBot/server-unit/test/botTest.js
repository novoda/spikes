var should = require('should');
var io = require('socket.io-client');
var test = require('unit.js');

var socketURL = 'http://0.0.0.0:5000'

var options ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=bot'
};

describe("TelepresenceBot Server: BotTest ",function() {

    it('Should add bot to list of bots on connection.', function(done) {
        var bot = io.connect(socketURL, options);

        bot.on('connected', function(actualConnectedBots){
            var expectedConnectedBots = [bot.id];
            test.array(actualConnectedBots)
                .is(expectedConnectedBots);

            bot.disconnect();
            done();
        });
    });

    it('Should remove bot from list of bots on disconnection.', function(done) {
        var testObserver = io.connect(socketURL, options);
        var bot = io.connect(socketURL, options);

        bot.on('connected', function(){
            bot.disconnect();
        });

        testObserver.on('disconnected_bots', function(actualConnectedBots) {
            test.array(actualConnectedBots)
                .isEmpty();

            testObserver.disconnect();
            done();
        });
    });

});
