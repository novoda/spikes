module.exports = {
  rule: mostCommonWords
}

function mostCommonWords(messages) {
  var words = messages.map(each => {
      return each.text;
  }).reduce((prev, curr) => {
    return prev + ' ' + curr;
  }).replace(',', '').split(' ');

  var frequency = {};  // array of frequency.
  var max = 0;  // holds the max frequency.
  var result;   // holds the max frequency element.
  for(var v in words) {
          frequency[words[v]]=(frequency[words[v]] || 0)+1; // increment frequency.
          if(frequency[words[v]] > max) { // is this frequency > max so far ?
                  max = frequency[words[v]];  // update max.
                  result = words[v];          // update result.
          }
  }
  return {
    word: result,
    count: max
  }
}
