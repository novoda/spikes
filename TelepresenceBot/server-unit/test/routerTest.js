var sinon = require('sinon'),
    expect = require('chai').expect,
    botLocator = require('../core/BotLocator.js')(),
    router = require('../core/Router.js')(botLocator);

var queryWithHuman = {
    clientType: 'human',
    room: 'London',
};

var queryWithBot = {
    clientType: 'bot',
    room: 'London',
};

var queryWithUnhandled = {
    clientType: 'unhandled',
    room: 'London',
};

describe("Router Test", function () {

    it("Should replace 'query.room' with first available bot when 'clientType' is 'Human'.", function (done) {
        var firstAvailableBotId = 'ABCDEFGH123';

        mockBotLocator = sinon.stub(botLocator, 'locateFirstAvailableBotIn')
                              .callsFake(function(){ return firstAvailableBotId; });

        router.route(queryWithHuman, onNext = function(data){
            expect(data).to.be.undefined;
            expect(mockBotLocator.called).to.be.true;
            expect(queryWithHuman.room).to.equal(firstAvailableBotId);
            botLocator.locateFirstAvailableBotIn.restore();
            done();
        });

    });


    it("Should pass through Bot without any checks.", function (done) {
        router.route(queryWithBot, onNext = function(data){
            expect(data).to.be.undefined;
            done();
        });

    });

    it("Should return 'Error' when the are no available 'Bots'.", function (done) {
        var noAvailableBots = undefined;

        mockBotLocator = sinon.stub(botLocator, 'locateFirstAvailableBotIn')
                              .callsFake(function(){ return noAvailableBots; });

        router.route(queryWithHuman, onNext = function(data){
            expect(mockBotLocator.called).to.be.true;
            expect(data.message).to.equal('No bots available');
            botLocator.locateFirstAvailableBotIn.restore();
            done();
        });

    });

    it("Should return 'Error' for unhandled 'ClientType'.", function (done) {
        router.route(queryWithUnhandled, onNext = function(data){
            expect(data.message).to.equal('Unrecognised clientType: unhandled');
            done();
        });
    });

});
