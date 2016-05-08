function waitForWindow(callback) {
  if (document.readyState === 'interactive' || document.readyState === 'complete') {
      callback();
  } else {
    document.addEventListener('DOMContentLoaded', callback, false);
  }
};

module.exports = {
  waitForWindow: waitForWindow
};
