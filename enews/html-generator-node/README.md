#### What is this?
Node module to generate a newsletter for enews.
The directory contains a dependable module and a simple `demo.js` to show how the module works.

#### Demo usage -

```
export token=your-slack-api-token
npm install && npm start
```

This will generate an `enews.html` file in the project directory.

#### External module usage -

Add the local dependency to your package.json
`"html-generator": "file:path-to/html-generator-node"`

```javascript
var generateHtml = require('html-generator').generateHtml;
var token = process.env.token; // or any other way you would like to provide the slack api token
var enewsFetcher = new EnewsFetcher(token);

enewsFetcher.getLastSevenDays(function(eNews) {
  var html = generateHtml(eNews, 'http://myunsubscribelink.com');
  console.log(html);
});
```
