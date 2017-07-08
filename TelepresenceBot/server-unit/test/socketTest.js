var chai = require('chai'),
    mocha = require('mocha'),
    expect = chai.expect;

var io = require('socket.io-client');

var socketUrl = 'http://localhost:4200';

unsupportedClientTypeOptions = {
    transports: ['websocket'],
    'force new connection': true
};

supportedClientTypeOptions = {
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human'
};

describe("TelepresenceBot Server: Routing Test", function () {

    beforeEach(function (done) {
        server = require('../core/testServer').server;
        done();
    });

    it("Should throw an error when attempting to connect an unsupported client type.", function (done) {
        var client = io.connect(socketUrl, unsupportedClientTypeOptions);

        client.on('error', function(errorMessage){
            expect(errorMessage).to.equal("Unrecognised clientType: undefined");

            client.disconnect();
            done();
        });
    });

    it("Should emit connected response when connecting a supported client type.", function (done) {
        var client = io.connect(socketUrl, supportedClientTypeOptions);

        client.once("connect", function () {
            client.disconnect();
            done();
        });
    });

});
