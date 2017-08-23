var expect = require('chai').expect,
    Mover = require('../core/mover.js');

var clientsAndRooms = {
    'humanId': {
        'botId': true
    },
    'botId': {
        'botId': true, 'London': true
    }
}

var emitter = {
    called: false,
    to: function (room) {
        return {
            emit: function (event, valueToEmit) {
                emitter.room = room;
                emitter.event = event;
                emitter.valueToEmit = valueToEmit;
                emitter.called = true;
            }
        }
    }
}

afterEach(function (done) {
    emitter.called = false;
    done();
});

describe('Mover Tests.', function () {

    it('Should emit to botId when moving humanId in any direction.', function (done) {
        var mover = new Mover(clientsAndRooms, emitter);

        mover.moveIn('humanId', 'forward');

        expect(emitter.called).to.be.true;
        expect(emitter.room).to.equal('botId');
        done();
    });

    it('Should emit event of direction when moving humanId in any direction.', function (done) {
        var mover = new Mover(clientsAndRooms, emitter);

        mover.moveIn('humanId', 'forward');

        expect(emitter.called).to.be.true;
        expect(emitter.event).to.equal('direction');
        done();
    });

    it('Should emit value of forward when moving humanId in a forward direction.', function (done) {
        var mover = new Mover(clientsAndRooms, emitter);

        mover.moveIn('humanId', 'forward');

        expect(emitter.called).to.be.true;
        expect(emitter.valueToEmit).to.equal('forward');
        done();
    });

    it('Should do nothing when a given clientId does not belong to any rooms.', function (done) {
        var mover = new Mover(clientsAndRooms, emitter);

        mover.moveIn('clientId', 'forward');

        expect(emitter.called).to.be.false;
        done();
    });

});
