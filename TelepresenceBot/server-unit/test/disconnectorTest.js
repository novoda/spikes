var expect = require('chai').expect,
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

describe("Disconnector Test",function() {

    it('Should return false when cannot locate room in list of rooms.', function(done){
        var disconnector = new Disconnector(rooms, connectedClients);

        var disconnected = disconnector.disconnectRoom('Room not present');

        expect(disconnected).to.be.false;
        done();
    });

    it('Should return false when connected clients do not contain room client.', function(done){
        var disconnector = new Disconnector(rooms, noConnectedClients);

        var disconnected = disconnector.disconnectRoom('London');

        expect(disconnected).to.be.false;
        done();
    });

    it('Should return true when disconnecting all clients in room.', function(done){
        var disconnector = new Disconnector(rooms, connectedClients);

        var disconnected = disconnector.disconnectRoom('London');

        expect(disconnected).to.be.true;
        done();
    });

    it('Should call disconnect on connected client when disconnecting all clients in room.', function(done){
        var disconnector = new Disconnector(rooms, {
            'botId': {
                disconnect: function() {
                    done();
                }
            }
        });

        disconnector.disconnectRoom('London');
    });

});
