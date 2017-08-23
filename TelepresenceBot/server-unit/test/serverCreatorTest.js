var sinon = require('sinon'),
    expect = require('chai').expect,
    router = require('../core/Router.js')(),
    disconnector = require('../core/Disconnector.js')(),
    observer = require('../core/Observer.js'),
    mover = require('../core/mover.js')(),
    ServerCreator = require('../core/serverCreator.js');

var io = require('socket.io-client');

var socketUrl = 'http://localhost:4200';

options = {
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human&room=London'
};

describe('ServerCreator Tests.', function () {

    before(function (done) {
        mockRouter = sinon.stub(router, 'route').callsFake(function (client, next) { return next(); });
        mockMover = sinon.stub(mover, 'moveIn').callsFake(function (clientId, direction) { return true; });
        mockDisconnector = sinon.stub(disconnector, 'disconnectRoom').callsFake(function () { return true; });
        done();
    });

    beforeEach(function (done) {
        server = new ServerCreator()
            .withRouter(router)
            .withDisconnector(disconnector)
            .withObserver(observer)
            .withMover(mover)
            .create();

        done();
    });

    afterEach(function (done) {
        server.close();
        done();
    });

    it('Should delegate to Router when client is connected.', function (done) {
        var client = io.connect(socketUrl, options);

        client.once('connect', function () {
            client.once('joined_room', function (room) {
                expect(mockRouter.called).to.be.true;
                done();
            });
        });
    });

    it('Should emit joined_room when client is connected.', function (done) {
        var client = io.connect(socketUrl, options);

        client.once('connect', function () {
            client.once('joined_room', function (room) {
                expect(room).to.equal('London');
                done();
            });
        });
    });

    it('Should delegate to Mover when moving in given direction.', function (done) {
        var client = io.connect(socketUrl, options);

        client.once('connect', function () {
            client.emit('move_in', 'forward');
        });

        observer.notify = function (eventName, data) {
            expect(mockMover.called).to.be.true;
            expect(mockMover.callCount).to.equal(1);
            expect(data).to.equal('forward');
            done();
        };
    });

    it('Should delegate to Disconnector when disconnecting an already connected client.', function (done) {
        var client = io.connect(socketUrl, options);

        client.once('connect', function () {
            client.disconnect();
        });

        observer.notify = function (eventName, data) {
            expect(mockDisconnector.called).to.be.true;
            expect(mockDisconnector.callCount).to.equal(1);
            done();
        };
    });

});
