var sinon = require('sinon'),
    chai = require('chai'),
    mocha = require('mocha'),
    expect = chai.expect,
    debug = require('debug')('socketTest'),
    router = require('../core/Router.js')(),
    disconnector = require('../core/Disconnector.js')(),
    observer = require('../core/Observer.js'),
    ServerCreator = require('../core/serverCreator.js');

var io = require('socket.io-client');

var socketUrl = 'http://localhost:4200';

options = {
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human&room=London'
};

describe("TelepresenceBot Server: Routing Test", function () {

    before(function(done){
        mockRouter = sinon.stub(router, 'route').callsFake(function(client, next){ return next(); });
        mockDisconnector = sinon.stub(disconnector, 'disconnectRoom').callsFake(function() { return true; });
        done();
    });

    beforeEach(function (done) {
        server = new ServerCreator()
               .withRouter(router)
               .withDisconnector(disconnector)
               .withObserver(observer)
               .create();

        debug('server starts');
        done();
    });

    afterEach(function(done) {
        server.close();
        debug('server closes');
        done();
    });

    it("Should delegate to 'Router' when client is connected.", function (done) {
        var client = io.connect(socketUrl, options);

        client.once("connect", function () {
            client.once("joined_room", function(room) {
                expect(mockRouter.called).to.equal(true);
                done();
            });
        });
    });

    it("Should emit 'joined_room' when client is connected.", function (done) {
        var client = io.connect(socketUrl, options);

        client.once("connect", function () {
            client.once("joined_room", function(room) {
                expect(room).to.equal("London");
                done();
            });
        });
    });

    it("Should delegate to 'Disconnector' when disconnecting an already connected client.", function (done) {
        var client = io.connect(socketUrl, options);

        client.once("connect", function () {
            client.disconnect();
        });

        observer.notify = function(eventName, data) {
            debug(eventName, data);
            expect(mockDisconnector.called).to.equal(true);
            expect(mockDisconnector.callCount).to.equal(1);
            done();
        };
    });

});
