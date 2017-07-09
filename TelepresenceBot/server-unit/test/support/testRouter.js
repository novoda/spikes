var willRoute = false;

function TestRouter() {

}

TestRouter.prototype.route = function(client, next) {
    if(willRoute) {
        return next();
    } else {
        return next(new Error("Will not route"));
    }
}

TestRouter.prototype.willNotRoute = function() {
    willRoute = false;
}

TestRouter.prototype.willRoute = function() {
    willRoute = true;
}

module.exports = TestRouter;


