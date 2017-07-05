`Nodejs` app written in `TypeScript` to check the open PRs of a github repository and ping slack if they're deemed unmergable.


## Usage

Create a secrets.json in the root with the following structure:

```json
{
  "gitHub": {
    "token": "",
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
npm run build_run
```

EZ PZ
