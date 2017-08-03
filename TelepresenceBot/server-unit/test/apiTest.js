var mocha = require('mocha'),
    request = require('supertest'),
    expect = require('chai').expect,
    fs = require('fs'),
    ServerCreator = require('../core/serverCreator.js');

var io = require('socket.io-client');

var server, options = {
    transports: ['websocket'],
    'force new connection': true
};

describe('API Tests - Performing GET requests.', function () {

    beforeEach(function (done) {
        server = new ServerCreator().create();
        done();
    });

    afterEach(function(done) {
        server.close(done);
    });

    it('Should serve index.html in response to "/" call.', function (done) {
        request(server)
            .get('/')
            .type('html')
            .expect(200)
            .end(function(error, response) {
                var file = fs.readFileSync('../core/html/index.html', 'utf8');
                expect(response.text).to.equal(file);
                done();
            });
    });

    it('Should serve rooms.json in response to "/rooms" call.', function (done) {
        request(server)
            .get('/rooms')
            .type('json')
            .expect(200)
            .end(function(error, response) {
                var file = fs.readFileSync('../core/json/rooms.json', 'utf8');
                expect(response.text).to.equal(file);
                done();
            });
    });

    it('Should respond with 404 for an invalid endpoint.', function testPath(done) {
        request(server)
            .get('/foo/bar')
            .expect(404, done);
    });

});
