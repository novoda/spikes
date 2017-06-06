import express from 'express'
import bodyParser from 'body-parser'
import config from './config.js'
import TV from './tv.js'
import ActionResolver from './actionResolver.js'

const tv = new TV(config)
const actionResolver = new ActionResolver(tv)
const app = express()
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

const server = app.listen(process.env.PORT || 5000, () => {
  console.log('Express server listening on port %d in %s mode', server.address().port, app.settings.env)
})

app.post('/webhook', async (request, response) => {
  const requestBody = request.body.result
  console.log(requestBody)
  const result = await actionResolver.resolve(requestBody)
  return response.json(result)
})
