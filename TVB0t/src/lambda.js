import TV from './tv.js'

exports.webhook = async (event, context, callback) => {
  const config = require('./config.js')
  const requestBody = JSON.parse(event.body).result
  const result = await new TV(config).fetch(requestBody)
  callback(null, {statusCode: 200, headers: {}, body: JSON.stringify(result)})
}
