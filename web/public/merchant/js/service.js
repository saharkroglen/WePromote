'use strict';

/* Services */

angular.module('wePromote.services', ['ngResource'])
.factory('Reset', function ($http, $q) {
  var rest = {};
  
  rest.getSocialInfo = function (cid) {
    return $http({
      url: 'https://graph.facebook.com/fql?q=SELECT%20url,%20normalized_url,%20share_count,%20like_count,%20comment_count,%20total_count,commentsbox_count,%20comments_fbid,%20click_count%20FROM%20link_stat%20WHERE%20url=%27http://itripy.com/wepromote/campaigns/?cid='+cid+'%27'
    });
  };
  
  return rest;
});









