var intervalId;

var socketOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human&room=London'
};

var socket = io(socketOptions);

$(document).ready(function(){

    $("input").mousedown(function(){
        console.log("mouseDown");
        var message = $(this).val();
        intervalId = setInterval(function() {
            sendMessage(message);
        }, 100);
    });

    function sendMessage(message) {
        socket.emit('chat message', message);
        $('#messages').append($('<li>').text(message));
    }

    $("input").mouseup(function(){
        console.log("mouseUp");
        clearInterval(intervalId);
    });

});
