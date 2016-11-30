const google = require('googleapis');
const sheets = google.sheets('v4');

const key = require('./config.json').google;
const jwtClient = new google.auth.JWT(
  key.client_email,
  null,
  key.private_key,
  ['https://www.googleapis.com/auth/spreadsheets'],
  null
);

jwtClient.authorize(function (err, tokens) {
  if (err) {
    console.log(err);
    return;
  }

  updatePair({first: '@ouchadam', second: '@ataulm'});

});

function updatePair(pair) {
  getPairingGrid().then(grid => {
    if (gridContainsPair(grid, pair)) {
      return withPair(pair)(grid);
    } else {
      return addPairToGrid()
      .then(getPairingGrid)
      .then(withPair(pair));
    }
  }).then(updateGridPositions());
  .catch(console.log);
}

function withPair(pair) {
  return function(grid) {
    return Promise.resolve( {
      grid: grid,
      pair: pair
    } );
  };
}

function updateGridPositions(pair, grid) {

}

function addPairToGrid() {
  // todo
}

function gridContainsPair(grid, pair) {
  return grid.columns.contains(pair.first) &&
    grid.columns.contains(pair.second) &&
    grid.rows.contains(pair.first) &&
    grid.rows.contains(pair.second);
}

function getPairingGrid() {
  return get({
    auth: jwtClient,
    spreadsheetId: key.spreadsheet,
    ranges: ['automatic!A2:A','automatic!B1:1']
  }).then(parseGrid);
}

function parseGrid(data) {
  const ranges = data.valueRanges;
  return Promise.resolve( {
    columns: ranges[0].values,
    rows: ranges[1].values
  } );
}

function get(options) {
  return new Promise(function(resolve, reject) {
    sheets.spreadsheets.values.batchGet(options, function(err, response) {
      if (err) {
        reject(err);
      } else {
        resolve(response);
      }
    });
  });
}
