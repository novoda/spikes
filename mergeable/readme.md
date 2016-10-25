Nodejs app to check the open PRs of a github repository and ping slack if they're deemed unmergable.


## Usage

Create a secrets.json with the following structure:

```json
{
  "gitHub": {
    "credentials": {
      "username": "",
      "password": ""
    },
    "repoOwner": "",
    "repoName": ""
  },
  "slack": {
    "token": "",
    "recipient": ""
  }
}
```

```
npm install
node index.js
```

EZ PZ
