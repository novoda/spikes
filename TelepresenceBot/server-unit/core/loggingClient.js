function LoggingClient() {
}

LoggingClient.prototype.emit = function(event, dataToEmit) {
    console.log("\nEvent Emitted: " + event + "\nData Emitted: " + dataToEmit + "\n");
}

module.exports = LoggingClient;


