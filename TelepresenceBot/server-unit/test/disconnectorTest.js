var chai = require('chai'),
    mocha = require('mocha'),
    expect = chai.expect,
    debug = require('debug')('disconnectorTest'),
    Disconnector = require('../core/disconnector.js');

var rooms = {
    'botId': {
        sockets: {'botId':true},
        length:1
    },
    'London': {
        sockets: {'botId':true},
        length:1
    }
};

var connectedClients = {
    'botId': {
        disconnect: function(){}
    }
};

var noConnectedClients = {};

describe("Disconnector",function() {

    it('Should return false when cannot locate room in list of rooms.', function(done){
        var disconnector = new Disconnector(rooms, connectedClients);

        var disconnected = disconnector.disconnectRoom('Room not present');

        expect(disconnected).to.equal(false);

        done();
    });

    it('Should return true when disconnecting all clients in room.', function(done){
        var disconnector = new Disconnector(rooms, connectedClients);

        var disconnected = disconnector.disconnectRoom('London');

        expect(disconnected).to.equal(true);

        done();
    });

    it('Should return false when connected clients do not contain room client.', function(done){
        var disconnector = new Disconnector(rooms, noConnectedClients);

        var disconnected = disconnector.disconnectRoom('London');

        expect(disconnected).to.equal(false);

        done();
    });

});
