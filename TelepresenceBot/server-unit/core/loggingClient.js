var debug = require('debug')('loggingClient');

function LoggingClient(client) {
    this.client = client;
}

LoggingClient.prototype.emit = function(event, dataToEmit) {
    if(this.client != undefined) {
        this.client.emit(event, dataToEmit);
    }
    debug("\nEvent Emitted: ", event);

    if(dataToEmit != undefined && dataToEmit.length > 0) {
        debug("Data Emitted: ", dataToEmit);
    }
}

module.exports = LoggingClient;


