const GoogleSheets = require('./google-sheets');

const ALPHA =["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
const TEMPLATE_OFFSET = 1;
const ONE_INDEXED = 1;

function Spreadsheet(config) {
  this.googleSheets = new GoogleSheets(config);
  this.spreadsheet = config.spreadsheet;
}

Spreadsheet.prototype.update = function(pair) {
  const self = this;
  return self.googleSheets.authenticate()
  .then(getPairingGrid(self))
  .then(addPairNamesToGridIfNeeded(self, pair))
  .then(addPeeps(self, pair));
}

function getPairingGrid(self) {
  return function() {
    return self.googleSheets.get({
      spreadsheetId: self.spreadsheet,
      ranges: ['automatic!A2:A','automatic!B1:1']
    }).then(parseGrid);
  };
}


function addPairNamesToGridIfNeeded(self, pair) {
  return function(grid) {
    if (gridContainsPair(grid, pair)) {
      return Promise.resolve(grid);
    } else {
      return addPairToGrid(self, grid, pair)
        .then(getPairingGrid(self));
    }
  };
}

function addPairToGrid(self, grid, pair) {
  const endRowIndex = indexToRow(grid.rows.length);
  const first = toPersonCells(indexToColumn(grid.columns.length), endRowIndex);
  const second = toPersonCells(indexToColumn(grid.columns.length + 1), endRowIndex + 1);

  console.log(first);
  console.log(second);
  const requests = [
    toAddPersonRequest(pair.first, first[0]),
    toAddPersonRequest(pair.first, first[1]),
    toAddPersonRequest(pair.second, second[0]),
    toAddPersonRequest(pair.second, second[1]),
  ];
  return update(self, requests);
}

function addPerson(name, cell) {
  return update(name, cell);
}

function toAddPersonRequest(name, cell) {
  return { value: name, cell: cell }
}

function toPersonCells(column, row) {
  return [ column + TEMPLATE_OFFSET, ALPHA[0] + row ];
}

function addPeeps(self, pair) {
  return function(grid) {
    if (gridContainsPair(grid, pair)) {
      return Promise.resolve(asIndexes(grid, pair))
        .then(updateGrid(self));
    } else {
      throw 'Pair is missing, make sure to add before updating the grid'
    }
  }
}

function asIndexes(grid, pair) {
  return {
    first: {
      name: pair.first,
      indexes: findPersonIndex(grid, pair.first)
    },
    second: {
      name: pair.second,
      indexes: findPersonIndex(grid, pair.second)
    }
  };
}

function findPersonIndex(grid, person) {
  return {
    rowIndex: grid.rows.indexOf(person),
    columnIndex: grid.columns.indexOf(person)
  };
}

function updateGrid(self, indexes) {
  return function(indexes) {
    const cell1 = findCrossoverCell(indexes.first, indexes.second);
    const cell2 = findCrossoverCell(indexes.second, indexes.first);
    return markPaired(self, [cell1, cell2]);
  };
}

function findCrossoverCell(first, second) {
  const row = indexToRow(first.indexes.rowIndex);
  const column = indexToColumn(second.indexes.columnIndex);
  return column + row;
}

function indexToRow(index) {
  return index + TEMPLATE_OFFSET + ONE_INDEXED;
}

function indexToColumn(index) {
  return ALPHA[index + TEMPLATE_OFFSET];
}

function withPair(pair) {
  return function(grid) {
    return Promise.resolve( {
      grid: grid,
      pair: pair
    } );
  };
}

function markPaired(self, cells) {
  return update(self, cells.map(toMarkPairedRequest));
}

function toMarkPairedRequest(cell) {
  return { value: 'X', cell: cell }
}

function update(self, data) {
  const resource = {
    valueInputOption: 'USER_ENTERED',
    data: data.map(toData)
  };
  return self.googleSheets.put({
    spreadsheetId: self.spreadsheet,
    resource: resource
  });
}

function toData(request) {
  return {
    range: `automatic!${request.cell}:${request.cell}`,
    majorDimension: 'ROWS',
    values: [[request.value]]
  };
}

function gridContainsPair(grid, pair) {
  return grid.columns.includes(pair.first) &&
    grid.columns.includes(pair.second) &&
    grid.rows.includes(pair.first) &&
    grid.rows.includes(pair.second);
}

function parseGrid(data) {
  const ranges = data.valueRanges;
  return Promise.resolve( {
    columns: flatten(ranges[0].values),
    rows: flatten(ranges[1].values)
  } );
}

function flatten(array) {
  return [].concat.apply([], array);
}

module.exports = Spreadsheet;
