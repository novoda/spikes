if (widgets.xmasCountdown) throw "XmasCountdown already exists";

widgets.xmasCountdown = function() {
  return {
    present: function(data, element) {
      console.log(data);
      element.innerHTML = presentXmasCountdown(data.payload);
    }
  };
}

function presentXmasCountdown(data) {
  return '<div class="content-container active-image">' +
  '<div class="active-container">' +
    '<div class="active-filler"></div>' +
    '<span class="active-channel-copy review"><b>' + data.daysUntilChristmas + '</b><br><br>' +
    '<b>' + 'days until christmas!' + '</b><br><br>' +
    '' + '🎅' + '</span>' +
  '</div>' +
'</div>';
}
