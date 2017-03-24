var express = require('express');
var app = express();

var port = process.env.PORT || 8080;

var router = express.Router();

router.get('/', function(req, res) {
    res.json({ message: 'Welcome to the TelepresenceBot api' });
});

router.route('/rooms')
    .get(function(req, res) {
        res.sendFile(__dirname + '/json/rooms.json');
    });

app.use('/api', router);

app.listen(port);
console.log('Starting server on ' + port);
