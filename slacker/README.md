Slacker
===

## Set up

In order to run Slacker locally you'll need to have nodejs (and npm) installed. If you don't, go do it now! 

To run the server, first make sure you have all the dependencies installed by running `npm install` under the "slacker" dir.
Then create a `config.json` in the `slacker dir` and specify the following: 

```json
{
  "server": {
    "port": ""
  },
  "widgets": {
    "sonar": {
      "token": ""
    },
    "slack": {
      "token": ""
    }
  }
}
```

As soon as all the dependencies and the config is set up, just type `npm start` into your terminal.

## Developing

**TODO**
