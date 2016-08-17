if (widgets.gallery) throw "Gallery already exists";

widgets.gallery = function() {
  return {
    present: function(data, element) {
      element.innerHTML = presentGallery(data.payload);
    }
  };
}

function presentGallery(data) {
  return '<div class="content-container">' +
    '<div class="text-shadow"></div>' +
    '<div class="avatar-bar">' +
      '<img class="avatar" src="' + data.user.profile.image_512 + '"></img>' +
      '<div class="avatar-text-container">' +
        '<span class="user white">' + data.user.real_name + '</span>' +
        '<span class="at-user white">@' + data.user.name + '</span>' +
        '<br>' +
        '<span class="user-title white">' + data.user.profile.title + '</span>' +
      '</div>' +
    '</div>' +
    '<img class="image" src="' + data.gallery.url + '"></img>' +
  '</div>';
}
