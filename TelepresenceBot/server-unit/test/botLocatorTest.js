var should = require('should');
var test = require('unit.js');
var BotLocator = require("../core/botLocator.js");

var botWithNoOtherSocketsInRoom = {
    'botId': {
        sockets: {'botId':true},
        length:1
    },
    'London': {
        sockets: {'botId':true},
        length:1
    }
};

describe("BotLocator ",function() {

    it('Should give undefined when bot is not found in given room.', function(done){
        var botLocator = new BotLocator(botWithNoOtherSocketsInRoom);

        var bot = botLocator.locateFirstAvailableBotIn("Unexpected Room");

        test.value(bot)
            .isUndefined();

        done();
    });

    it('Should give bot id when bot is found in given room.', function(done){
        var botLocator = new BotLocator(botWithNoOtherSocketsInRoom);

        var bot = botLocator.locateFirstAvailableBotIn("London");

        test.string(bot)
            .is("botId");

        done();
    });

});
