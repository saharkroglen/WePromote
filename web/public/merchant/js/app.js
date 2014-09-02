'use strict';

/* App Module */
Parse.initialize("IwjXCfulcA64BVZd002616T56rLALjuhGnBdIRCn", "1PK4dWFuMOopioVSiS0jIyiUTUMwj50SjxY3n4kj");
if ( !Parse.User.current() ) location.href = './signin.html';
else if ( !Parse.User.current().authenticated() ) location.href = './signin.html';

var wePromote = angular.module('wePromote', [
  'ngRoute',
  'ngResource',
  'wePromote.services'
]);

wePromote.config(['$routeProvider', '$locationProvider',
  function($routeProvider, $locationProvider) {
    $routeProvider.
      when('/', {
        templateUrl: 'template/dashboard.html'
      }).
      when('/campaign-overview', {
        templateUrl: 'template/campaign-overview.html'
      }).
      when('/add-campaign', {
        templateUrl: 'template/add-campaign.html'
      }).
      when('/edit-campaign', {
        templateUrl: 'template/edit-campaign.html'
      }).
      when('/invitation-overview', {
        templateUrl: 'template/invitation-overview.html'
      }).
      when('/invite-promoter', {
        templateUrl: 'template/invite-promoter.html'
      }).
      otherwise({
        redirectTo: '/'
      });
  }]);
