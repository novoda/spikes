var chai = require('chai'),
    mocha = require('mocha'),
    should = chai.should();

var io = require('socket.io-client');

var server, options = {
    transports: ['websocket'],
    'force new connection': true
};

describe("echo", function () {

    beforeEach(function (done) {
        server = require('../core/testServer').server;
        done();
    });

    it("echos message", function (done) {
        var client = io.connect("http://localhost:4200", options);

        client.once("connect", function () {
            client.once("echo", function (message) {
                message.should.equal("Hello World");

                client.disconnect();
                done();
            });

            client.emit("echo", "Hello World");
        });
    });

});
