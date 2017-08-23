var expect = require('chai').expect,
    Disconnector = require('../core/disconnector.js');

var rooms = {
    'botId': {
        sockets: { 'botId': true },
        length: 1
    },
    'botId02': {
        sockets: { 'botId02': true },
        length: 1
    },
    'London': {
        sockets: { 'botId': true, 'botId02': true },
        length: 2
    }
};

var connectedClients = {
    called: false,
    'botId': {
        disconnect: function () {
            connectedClients.called = true;
        }
    }
};

var noConnectedClients = {
    called: false,
    disconnect: function () {
        noConnectedClients.called = true;
    }
};

afterEach(function (done) {
    connectedClients.called = false;
    noConnectedClients.called = false;
    done();
});

describe('Disconnector Tests.', function () {

    it('Should do nothing when cannot locate room in list of rooms.', function (done) {
        var disconnector = new Disconnector(rooms, connectedClients);

        disconnector.disconnectRoom('Room not present');

        expect(connectedClients.called).to.be.false;
        done();
    });

    it('Should do nothing when connected clients does contain any clients.', function (done) {
        var disconnector = new Disconnector(rooms, noConnectedClients);

        disconnector.disconnectRoom('London');

        expect(noConnectedClients.called).to.be.false;
        done();
    });

    it('Should call disconnect when disconnecting all clients in room.', function (done) {
        var disconnector = new Disconnector(rooms, connectedClients);

        disconnector.disconnectRoom('London');

        expect(connectedClients.called).to.be.true;
        done();
    });

});
