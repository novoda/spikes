import express from 'express'
import bodyParser from 'body-parser'
import TV from './tv.js'

const tv = new TV()
const app = express()
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

const server = app.listen(process.env.PORT || 5000, () => {
  console.log('Express server listening on port %d in %s mode', server.address().port, app.settings.env)
})

app.post('/webhook', async (request, response) => {
  const requestBody = request.body.result
  console.log(requestBody)
  const result = await tv.fetch(requestBody)
  return response.json(result)
})
