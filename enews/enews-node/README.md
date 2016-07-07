#### What is this?
Node module to fetch all the messages containing `enews` from the novoda general slack channel for a given date range or last seven days.
The directory contains a dependable module and a simple `demo.js` to show how the module works.

#### Demo usage -

export token=your-slack-api-token
npm install && npm start

#### External module usage -

Add the local dependency to your package.json
`"enews-fetcher": "file:path-to/enews-node"`

```javascript
var EnewsFetcher = require('enews-fetcher');
var token = process.env.token; // or any other way you would like to provide the slack api token
var enewsFetcher = new EnewsFetcher(token);

enewsFetcher.getLastSevenDays(function(eNews) {
  console.log(eNews);
});
```
