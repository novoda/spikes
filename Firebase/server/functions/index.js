var functions = require('firebase-functions');

// Makes all new messages ALL UPPERCASE.
exports.makeUppercase = functions.database()
    .path('/channels/global/{messageId}').on('write', function(event) {
      // Reference to the database object that triggered the function.
      // This reference is authorized as the user who initiated the write that triggered the function.
      var messageRef = event.data.ref();
      console.log('Reading firebase object at path: ' + messageRef.toString());

      // The Firebase database object that triggered the function.
      var messageDataValue = event.data.val();
      console.log('Message content: ' + JSON.stringify(messageDataValue));

      // Uppercase the message.
      var uppercased = messageDataValue.body.toUpperCase();

      // Saving the uppercased message to DB.
      console.log('Saving uppercased message: ' + uppercased);
      return messageRef.update({body: uppercased});
    });
