var blocks = ['block', 'block1', 'block2'];

var presentMostActive = function(position) {
  return '
  <div class="centered-horiz '+ block[position]' + ">
  <div>
      <div class="right semi-circle">
        <div class="content centered-vert">
          <h1 class="text title">#some channel name</h1>
          <p class="text summary">The most active channel</p>
        </div>
      </div>
      <img src="./public/slack.png" class="left avatar"></img>
    </div>
  </div>
  ';
}

this.presentMostActive = presentMostActive;
