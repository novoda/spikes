const functions = require('firebase-functions')
const admin = require('firebase-admin')
admin.initializeApp(functions.config().firebase);

const database = admin.database()

exports.redirect = functions.https.onRequest((request, response) => {
    const key = encodeURIComponent(request.originalUrl)
    console.log('Search for', key)
    database.ref('url')
        .child(key)
        .once('value')
        .then(result => result.val())
        .then(result => {
            console.log(result)
            if (result) {
                response.redirect(result)
            } else {
                response.status(500).send(`No match for ${key}`)                
            }
        })
})
