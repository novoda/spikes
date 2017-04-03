'use strict';

/* exported WebHelper */
function WebHelper(config) {
  this.config = config;
}

WebHelper.prototype.log = function(what) {
  console.log('[%s] %s', this.config.tag, what);
};
