var clientToDisconnect = undefined;

function TestDisconnector() {
}

TestDisconnector.prototype.disconnect = function(roomName) {
    clientToDisconnect.disconnect();
    return true;
}

TestDisconnector.prototype.willDisconnect = function(client) {
    clientToDisconnect = client;
}

module.exports = TestDisconnector;
