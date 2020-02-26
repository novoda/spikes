var intervalId;

const socketOptions ={
    transports: ['websocket'],
    'force new connection': true,
    query: 'clientType=human&room=London'
};

const socket = io(socketOptions);

$(document).ready(function(){

    $("input").mousedown(function(){
        console.log("mouseDown");
        const message = $(this).val();
        intervalId = setInterval(function() {
            sendMessage(message);
        }, 100);
    });

    function sendMessage(message) {
        socket.emit('move_in', message);
        $('#messages').append($('<li>').text(message));
    }

    $("input").mouseup(function(){
        console.log("mouseUp");
        clearInterval(intervalId);
    });

});
