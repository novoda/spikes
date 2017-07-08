var mocha = require('mocha'),
    request = require('supertest');

var io = require('socket.io-client');

var server, options = {
    transports: ['websocket'],
    'force new connection': true
};

describe("Performing GET request", function () {

    beforeEach(function (done) {
        delete require.cache[require.resolve('../core/testServer')];
        server = require('../core/testServer').server;
        done();
        console.log('server starts');
    });

    afterEach(function(done) {
        server.close(done);
        console.log('server closes');
    });

    it("responds to /", function (done) {
        request(server)
            .get('/')
            .expect(200, done);
    });

    it('404 everything else', function testPath(done) {
        request(server)
            .get('/foo/bar')
            .expect(404, done);
    });

});
