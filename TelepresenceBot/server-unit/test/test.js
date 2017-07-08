var mocha = require('mocha'),
    request = require('supertest'),
    debug = require('debug')('test'),
    expect = require('chai').expect,
    fs = require('fs')

var io = require('socket.io-client');

var server, options = {
    transports: ['websocket'],
    'force new connection': true
};

describe("Performing GET request", function () {

    beforeEach(function (done) {
        delete require.cache[require.resolve('../core/testServer')];
        server = require('../core/testServer').server;
        debug('server starts');
        done();
    });

    afterEach(function(done) {
        server.close(done);
        debug('server closes');
    });

    it("Should serve index.html in response to / call.", function (done) {
        request(server)
            .get('/')
            .type('html')
            .expect(200)
            .end(function(error, response) {
                var file = fs.readFileSync("../core/html/index.html", "utf8");
                expect(response.text).to.equal(file);
                done();
            });
    });

        it("Should serve rooms.json in response to /rooms call.", function (done) {
            request(server)
                .get('/rooms')
                .type('json')
                .expect(200)
                .end(function(error, response) {
                    var file = fs.readFileSync("../core/json/rooms.json", "utf8");
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
