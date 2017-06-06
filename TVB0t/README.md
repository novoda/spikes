TVB0t
===

This is a two weeks experimentation on a chat bot (google home, slack...) for TV using api.ai

Debugging locally
---

In one terminal  
`> npm run server`

In another one   
`> npm run client`

On the client you can now type as if you were talking to the bot.  
Your message if going to be sent to `api.ai` to be interpreted and then the response is sent to your local server server which should respond accordingly.  

Deploying to AWS Lambda
---

`> ./gradlew build`

```
> aws lambda create-function \
--region eu-west-1 \
--function-name TVB0t  \
--zip-file fileb://build/distributions/TVB0t-1.0.zip \
--role arn:aws:iam::YOUR_ID:role/lambda_basic_execution \
--handler dist/lambda.webhook \
--runtime nodejs6.10 \
--profile default \
--timeout 10
```

To be accessible from the outside we need to add a trigger.  
In the Lambda console inside your lambda, add a trigger using `ApiGateway`.  
I used `LambdaMicroservice` / `prod` / `AWS IAM` and secured it with a key as explained [here](http://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-api-keys.html).   
You can then feed the url to your bot on api.ai in `Fulfillement`, don't forget the `x-api-key` header if you protected the endpoint.

And whenever you need to re-deploy:

`> ./gradlew build`

```
> aws lambda update-function-code \
--region eu-west-1 \
--function-name TVB0t  \
--zip-file fileb://build/distributions/TVB0t-1.0.zip \
--profile default
```
