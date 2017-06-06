import readline from 'readline'
import apiai from 'apiai'
import request from 'request-promise-native'

const rl = readline.createInterface(process.stdin, process.stdout)
const app = apiai('192bfd4ccb834a69a520dad01da03c20')

function send(message) {
  app.textRequest(message, {
    sessionId: 'some_unique_id'
  }).on('response', function (response) {
    print('api.ai response:', JSON.stringify(response, null, 2))
    if (response.result.action === 'input.unknown') {
      print('Oops there was an error!', 'Action has not been resolved by api.ai')
      rl.prompt()
    } else {
      sendToLocalServer(response)
    }
  }).on('error', function (error) {
    print('Oops there was an error!', error)
    rl.prompt()
  }).end()
}

function print(title, content) {
  console.log(title)
  console.log('========================')
  console.log(content)
}

function sendToLocalServer(response) {
  request({
    method: 'POST',
    uri: 'http://localhost:5000/webhook',
    body: response,
    json: true
  }).then(response => {
    print('local server response:', response || 'Got no response back :(')
    rl.prompt()
  })
}

rl.setPrompt('Type your message > ')
rl.prompt()

rl.on('line', function (message) {
  send(message)
}).on('close', function () {
  console.log('\n:wave:')
  process.exit(0)
})
