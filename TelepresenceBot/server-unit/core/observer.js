function Observer() {

}

Observer.prototype.observed = function(eventName, data) {
    return true;
}

module.exports = Observer;
