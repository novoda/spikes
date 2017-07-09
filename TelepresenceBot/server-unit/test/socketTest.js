var chai = require('chai'),
    mocha = require('mocha'),
    expect = chai.expect,
    debug = require('debug')('socketTest'),
    ServerCreator = require('../core/serverCreator.js')
    TestRouter = require('./support/testRouter.js');

var io = require('socket.io-client');

var socketUrl = 'http://localhost:4200';

unsupportedClientTypeOptions = {
    transports: ['websocket'],
    'force new connection': true
};

humanClientTypeOptions = {
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human'
};

var testClientTypeOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=test'
};

describe("TelepresenceBot Server: Routing Test", function () {

    beforeEach(function (done) {
        testRouter = new TestRouter();
        server = new ServerCreator(testRouter).create();
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
        var unsupportedClient = io.connect(socketUrl, unsupportedClientTypeOptions);

        unsupportedClient.on('error', function(errorMessage){
            expect(errorMessage).to.equal("Will not route");
            done();
        });
    });

    it("Should emit 'connect' when Routing is successful", function (done) {
        testRouter.willRoute();
        var testClient = io.connect(socketUrl, testClientTypeOptions);

        testClient.once("connect", function() {
            var client = io.connect(socketUrl, humanClientTypeOptions);

            client.once("connect", function () {
                testClient.disconnect();
                client.disconnect();
                done();
            });
        });
    });

    it("Should emit 'disconnect' when disconnecting an already connected client.", function (done) {
        testRouter.willRoute();
        var testClient = io.connect(socketUrl, testClientTypeOptions);

        testClient.once("connect", function() {
            var client = io.connect(socketUrl, humanClientTypeOptions);

            client.once("connect", function () {
                client.disconnect();
                testClient.disconnect();
            });

            client.once("disconnect", function(){
                done();
            });
        });
    });

});
