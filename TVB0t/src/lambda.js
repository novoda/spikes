import config from './config.js'
import TV from './tv.js'
import ActionResolver from './actionResolver.js'

exports.webhook = async (event, context, callback) => {
  const tv = new TV(config)
  const actionResolver = new ActionResolver(tv)
  const requestBody = JSON.parse(event.body).result
  const result = await actionResolver.resolve(requestBody)
  callback(null, {statusCode: 200, headers: {}, body: JSON.stringify(result)})
}
