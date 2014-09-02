'use strict';

/* Connection */

function statusChangeCallback(response) {
  if (response.status === 'connected') {
    
  } else if (response.status === 'not_authorized') {
    
  } else {
    
  }
}

function facebookWallPost(name, link, picture, caption, description) {
	var params = {
	  method: 'feed',
	  name: name,
	  link: link,
	  picture: picture,
	  caption: caption,
	  description: description
	};
  FB.ui(params, function(obj) { console.log(obj);});
}

function checkLoginState() {
  FB.getLoginStatus(function(response) {
    statusChangeCallback(response);
  });
}

window.fbAsyncInit = function() {
  Parse.FacebookUtils.init({
    appId      : '1527859724103673',
    cookie     : true,
    xfbml      : true,
    version    : 'v2.0'
  });
  
  FB.getLoginStatus(function(response) {
    statusChangeCallback(response);
  });
  
  FB.Event.subscribe('auth.authResponseChange', function(response) {
    removeCSSRule(".fb-comments > span");
    addCSSRule(".btnLoginToFb", "display:none !important;");
  });
  
  FB.Event.subscribe('auth.login', function(response) {
  
  });
  
  FB.api('/me', function(response) {
    if (!response.error) {
      setCookie('c_user', 1, 30, '/');
      setCookie('c_username', response.name, 30, '/');
    }
  });
};

(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/all.js";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));