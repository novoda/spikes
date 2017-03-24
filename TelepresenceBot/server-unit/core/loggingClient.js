function LoggingClient(client) {
    this.client = client;
}

LoggingClient.prototype.emit = function(event, dataToEmit) {
    if(this.client != undefined) {
        this.client.emit(event, dataToEmit);
    }
    console.log("\nEvent Emitted: ", event);

    if(dataToEmit != undefined && dataToEmit.length > 0) {
        console.log("Data Emitted: ", dataToEmit);
    }
}

module.exports = LoggingClient;


