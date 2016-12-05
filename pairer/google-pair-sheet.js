const GoogleSheets = require('./google-sheets');

const COLUMNS =["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
const TEMPLATE_OFFSET = 1;
const ONE_INDEXED = 1;

const sheetname = 'automatic';
const nameRow = "A2:A";
const nameColumn = "B1:1";

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
      ranges: [`${sheetname}!${nameRow}`,`${sheetname}!${nameColumn}`]
    }).then(parseGrid);
  };
}


function addPairNamesToGridIfNeeded(self, pair) {
  return function(grid) {
    return addPairToGrid(self, grid, pair)
      .then(getPairingGrid(self));
    }
}

function addPairToGrid(self, grid, pair) {
  const endRowIndex = indexToRow(grid.rows.length);
  const requests = pair.filter((person => !grid.columns.includes(person)))
                       .map((person, index) => {
                         return toPersonCells(
                           toAddPersonRequest(
                             person,
                             indexToColumn(grid.columns.length + index),
                             endRowIndex + index
                           )
                         );
                       }
                     ).map(flatten);
  return update(self, requests);
}

function addPerson(name, cell) {
  return update(name, cell);
}

function toAddPersonRequest(name, cell) {
  return { value: name, cell: cell }
}

function toPersonCells(column, row) {
  return [ column + TEMPLATE_OFFSET, COLUMNS[0] + row ];
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
  return pair.map((person) => {
    return findPersonIndex(grid, person);
  });
}

function findPersonIndex(grid, person) {
  return {
    rowIndex: grid.rows.indexOf(person),
    columnIndex: grid.columns.indexOf(person)
  };
}

function updateGrid(self) {
  return function(indexes) {
    const cells = indexes.map(function(index) {
      return indexes.filter(filterSelf(index))
                    .map(toPairCell(index));
    }).map(flatten);
    return markPaired(self, cells);
  };
}

function filterSelf(self) {
  return function(other) {
    return self !=== other;
  }
}

function toPairCell(index) {
  return function(thatIndex) {
    return findCrossoverCell(index, thatIndex));
  };
}

function findCrossoverCell(first, second) {
  const row = indexToRow(first.rowIndex);
  const column = indexToColumn(second.columnIndex);
  return column + row;
}

function indexToRow(index) {
  return index + TEMPLATE_OFFSET + ONE_INDEXED;
}

function indexToColumn(index) {
  return COLUMNS[index + TEMPLATE_OFFSET];
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
    range: `${sheetname}!${request.cell}:${request.cell}`,
    majorDimension: 'ROWS',
    values: [[request.value]]
  };
}

function gridContainsPair(grid, pair) {
  return pair.map(person => grid.columns.includes(person)
                  && grid.rows.includes(person))
                  .reduce((acc, el) => acc && el, true)
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
