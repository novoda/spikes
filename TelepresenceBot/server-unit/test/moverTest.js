var chai = require('chai'),
    mocha = require('mocha'),
    expect = chai.expect,
    debug = require('debug')('moverTest'),
    Mover = require('../core/mover.js');

var clientsAndRooms = {
    humanId: {
        botId: true
    },
    botId: {
        botId: true, London: true
    }
}

var emitter = {
    called: 0,
    to: function(room) {
        return {
            emit: function(event, valueToEmit) {
                emitter.room = room;
                emitter.event = event;
                emitter.valueToEmit = valueToEmit;
                emitter.called = 1;
            }
        }
    }
}

afterEach(function(done) {
    emitter.called = 0;
    done();
});

describe("Mover Test",function() {

    it("Should emit to 'botId' when moving 'humanId' in any direction.", function(done){
        var mover = new Mover(clientsAndRooms, emitter);

        mover.moveIn('humanId', 'forward');

        expect(emitter.room).to.equal('botId');
        done();
    });

    it("Should emit event of 'direction' when moving 'humanId in any direction.", function(done){
        var mover = new Mover(clientsAndRooms, emitter);

        mover.moveIn('humanId', 'forward');

        expect(emitter.event).to.equal('direction');
        done();
    });

    it("Should emit value of 'forward' when moving 'humanId' in a forward direction.", function(done){
        var mover = new Mover(clientsAndRooms, emitter);

        mover.moveIn('humanId', 'forward');

        expect(emitter.valueToEmit).to.equal('forward');
        done();
    });

    it("Should do nothing when a given 'clientId' does not belong to any rooms.", function(done){
        var mover = new Mover(clientsAndRooms, emitter);

        mover.moveIn('clientId', 'forward');

        expect(emitter.called).to.equal(0);
        done();
    });

});
