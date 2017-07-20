var sinon = require('sinon'),
    chai = require('chai'),
    mocha = require('mocha'),
    expect = chai.expect,
    debug = require('debug')('test'),
    botLocator = require('../core/BotLocator.js')(),
    router = require('../core/Router.js')(botLocator);

var queryWithHuman = {
    clientType: 'human',
    room: 'London',
};

var queryWithUnhandled = {
    clientType: 'unhandled',
    room: 'London',
};

describe("Router Test", function () {

    before(function(done) {
        done();
    });

    it("Should replace 'query.room' with first available bot when 'clientType' is 'Human'.", function (done) {
        var firstAvailableBotId = 'ABCDEFGH123';

        mockBotLocator = sinon.stub(botLocator, 'locateFirstAvailableBotIn')
                              .callsFake(function(){ return firstAvailableBotId; });

        router.route(queryWithHuman, onNext = function(data){
            expect(mockBotLocator.called).to.equal(true);
            expect(queryWithHuman.room).to.equal(firstAvailableBotId);
            done();
        });

    });

    it("Should return Error for unhandled ClientType.", function (done) {
        router.route(queryWithUnhandled, onNext = function(data){
            expect(data.message).to.equal('Unrecognised clientType: unhandled');
            done();
        });
    });

});
