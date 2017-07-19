var sinon = require('sinon'),
    chai = require('chai'),
    mocha = require('mocha'),
    expect = chai.expect,
    debug = require('debug')('socketTest'),
    ServerCreator = require('../core/serverCreator.js')
    TestRouter = require('./support/testRouter.js'),
    Disconnector = require('../core/Disconnector.js'),
    Observer = require('../core/Observer.js');

var io = require('socket.io-client');

var socketUrl = 'http://localhost:4200';

options = {
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human&room=London'
};

describe("TelepresenceBot Server: Routing Test", function () {

    beforeEach(function (done) {
        disconnector = new Disconnector();
        observer = new Observer();
        testRouter = new TestRouter();

        mockDisconnector = sinon.stub(disconnector, 'disconnectRoom').callsFake(function() { return true; });

        server = new ServerCreator(testRouter, disconnector, observer).create();
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

    it.only("Should delegate to 'Disconnector' when disconnecting an already connected client.", function (done) {
        var client = io.connect(socketUrl, options);

        testRouter.willRoute();

        client.once("connect", function () {
            client.disconnect();
        });

        observer.observed = function(eventName, data) {
            debug(eventName, data);
            expect(mockDisconnector.called).to.equal(true);
            expect(mockDisconnector.callCount).to.equal(1);
            done();
        };
    });

});
