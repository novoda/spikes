function Observer() {

}

Observer.prototype.notify = function(eventName, eventData) {
    return true;
}

module.exports = Observer;
