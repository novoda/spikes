var chai = require('chai'),
    mocha = require('mocha'),
    expect = chai.expect,
    debug = require('debug')('socketTest'),
    ServerCreator = require('../core/serverCreator.js')
    TestRouter = require('./support/testRouter.js'),
    TestDisconnector = require('./support/testDisconnector.js');

var io = require('socket.io-client');

var socketUrl = 'http://localhost:4200';

options = {
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human&room=London'
};

describe("TelepresenceBot Server: Routing Test", function () {

    beforeEach(function (done) {
        testRouter = new TestRouter();
        testDisconnector = new TestDisconnector();
        server = new ServerCreator(testRouter, testDisconnector).create();
        debug('server starts');
        done();
    });

    afterEach(function(done) {
        server.close();
        debug('server closes');
        done();
    });

    it("Should throw an error when Routing is unsuccessful.", function (done) {
        testRouter.willNotRoute();
        var unsupportedClient = io.connect(socketUrl, options);

        unsupportedClient.on('error', function(errorMessage){
            expect(errorMessage).to.equal("Will not route");
            done();
        });
    });

    it("Should emit 'joined_room' when Routing is successful", function (done) {
        testRouter.willRoute();

        var client = io.connect(socketUrl, options);

        client.once("connect", function () {
            client.once("joined_room", function(room) {
                expect(room).to.equal("London");
                done();
            });
        });
    });

    it("Should emit 'disconnect' when disconnecting an already connected client.", function (done) {
        var client = io.connect(socketUrl, options);

        testRouter.willRoute();
        testDisconnector.willDisconnect(client);

        client.once("connect", function () {
            client.disconnect();
        });

        client.once("disconnect", function(){
            done();
        });
    });

    it("Should emit 'disconnect' when disconnecting an already connected client.", function (done) {
        var client = io.connect(socketUrl, options);

        testRouter.willRoute();
        testDisconnector.willDisconnect(client);

        client.once("connect", function () {
            client.disconnect();
        });

        client.once("disconnect", function(){
            done();
        });
    });

});
