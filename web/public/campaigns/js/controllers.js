'use strict';

/* Controllers */

function LP ($scope, $http) {
  $scope.campaign = null;
  $scope.campaignId = null;
  $scope.deals = null;
  $scope.total = null;
  $scope.promoterName = null;
  $scope.amount = 1;
  $scope.discountApplied = false;
  Parse.initialize("IwjXCfulcA64BVZd002616T56rLALjuhGnBdIRCn", "1PK4dWFuMOopioVSiS0jIyiUTUMwj50SjxY3n4kj");
  var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
  var query = new Parse.Query(CampaignPromoter);
  if (getQueryParams('cid')) {
    query.equalTo("objectId", getQueryParams('cid'));
    query.find({
      success: function(results) {
        $scope.promoterName = results[0].attributes.PromoterId;
        $scope.campaignId = results[0].attributes.CampaignId;
        var Campaign = Parse.Object.extend("Campaign");
        var query = new Parse.Query(Campaign);
        query.equalTo("objectId", $scope.campaignId);
        query.find({
          success: function(res) {
            $scope.campaign = res[0];
            $scope.deals = $scope.campaign.attributes.deals;
            $scope.applyDiscountUpdate();
            $scope.$apply();          
          }
        });
        
      }
    });
  }
  
  $('.spinner .btn:first-of-type').on('click', function() {
    if (parseInt($('.spinner input').val()) >= 0) {
      $('.spinner input').val( parseInt($('.spinner input').val(), 10) + 1);
      $scope.amount = $scope.amount + 1;
      $scope.$apply();
    }
  });
  $('.spinner .btn:last-of-type').on('click', function() {
    if (parseInt($('.spinner input').val()) > 0) {
      $('.spinner input').val( parseInt($('.spinner input').val(), 10) - 1);
      $scope.amount = $scope.amount - 1;
      $scope.$apply();
    }
  });
  $scope.totalPrice = function (a,b) {
    $scope.total = a*b;
    return $scope.total;
  };
  $scope.selectedDeal = function () {
    return ($('#dealSelect').val() == "") ? false : true;
  };
  $scope.userStatus = function () {
    var isLoggedin = (getCookie('c_user') == "1") ? true : false;
    return isLoggedin;
  };
  $scope.pay = function () {
    
    $('.modal-body').height($('.modal-body').height())
    $('.modal-body').empty();
    $('.modal-body').append('<div class="glyphicon glyphicon-repeat loading"></div>');
    
    setTimeout(function () {
      $('.modal-body .loading').remove();
      $('.modal-body').append('<div>Congratulations!<br/>Your order has been placed and your confirm email was sent.<br/><br/>To redeem your deal, please contact the merchent and provide your order number: <b>1234</b><br/><br/>Want to share this deal with your friends and start winning rewards? <a hreh="#">You can start here now</a></div>');
    }, 3000);
    
  };
  
  window.updateScope = function () {
    $scope.$apply();
  };
  
  $scope.applyDiscount = ($scope.promoterName && ($('promoterId').val() != "" && $('promoterId').val() != " ")) ? true : false;
  $scope.applyDiscountUpdate = function () {
    $scope.applyDiscount = ($scope.promoterName && ($('#promoterId').val() != "" && $('#promoterId').val() != " ")) ? true : false;
    return $scope.applyDiscount;
  };
  $scope.apply = function (discount) {
    if ($scope.applyDiscountUpdate() && discount)
	{
      //alert('Your code was successfully applied. You saved '+discount+'% off! \nYou can now order and enjoy your special member price.')	  
	  $('#discountAppliedMessage').html('Your code was successfully applied. You saved '+discount+'% off! <br>You can now order and enjoy your special member price.');
	  $scope.discountApplied = true;
	  $('#discountAppliedMessage').show();
	}
	else
	{
		$scope.discountApplied = false;
		$('#discountAppliedMessage').hide();
	}
  };  
}

function AllCampaigns ($scope, $http) {
  $scope.camps = [];
  Parse.initialize("IwjXCfulcA64BVZd002616T56rLALjuhGnBdIRCn", "1PK4dWFuMOopioVSiS0jIyiUTUMwj50SjxY3n4kj");
  var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
  var query = new Parse.Query(CampaignPromoter);
  query.find({
    success: function(results) {
      $scope.camps = results;
      $scope.$apply();
    }
  });
}













// function LP ($scope, $http) {
//   $scope.campaign = null;
//   $scope.deals = null;
//   $scope.total = null;
//   $scope.amount = 1;
//   Parse.initialize("IwjXCfulcA64BVZd002616T56rLALjuhGnBdIRCn", "1PK4dWFuMOopioVSiS0jIyiUTUMwj50SjxY3n4kj");
//   var Campaign = Parse.Object.extend("Campaign");
//   var query = new Parse.Query(Campaign);
//   if (getQueryParams('cid')) {
//     query.equalTo("objectId", getQueryParams('cid'));
//     query.find({
//       success: function(results) {
//         $scope.campaign = results[0];
//         $scope.deals = $scope.campaign.attributes.deals;
//         $scope.$apply();
//         // $($('#dealSelect').children()[1]).prop('selected', true);
//         // $('#dealSelect').change();
//       },
//       error: function(error) {
//         console.log(error)
//       }
//     });
//   }
//   
//   $('.spinner .btn:first-of-type').on('click', function() {
//     if (parseInt($('.spinner input').val()) >= 0) {
//       $('.spinner input').val( parseInt($('.spinner input').val(), 10) + 1);
//       $scope.amount = $scope.amount + 1;
//       $scope.$apply();
//     }
//   });
//   $('.spinner .btn:last-of-type').on('click', function() {
//     if (parseInt($('.spinner input').val()) > 0) {
//       $('.spinner input').val( parseInt($('.spinner input').val(), 10) - 1);
//       $scope.amount = $scope.amount - 1;
//       $scope.$apply();
//     }
//   });
//   $scope.totalPrice = function (a,b) {
//     $scope.total = a*b;
//     return $scope.total;
//   };
//   $scope.selectedDeal = function () {
//     return ($('#dealSelect').val() == "") ? false : true;
//   };
//   $scope.userStatus = function () {
//     var isLoggedin = (getCookie('c_user') == "1") ? true : false;
//     return isLoggedin;
//   };
//   $scope.pay = function () {
//     
//     $('.modal-body').height($('.modal-body').height())
//     $('.modal-body').empty();
//     $('.modal-body').append('<div class="glyphicon glyphicon-repeat loading"></div>');
//     
//     setTimeout(function () {
//       $('.modal-body .loading').remove();
//       $('.modal-body').append('<div>Congratulations!<br/>Your order has been placed and your confirm email was sent.<br/><br/>To redeem your deal, please contact the merchent and provide your order number: <b>1234</b><br/><br/>Want to share this deal with your friends and start winning rewards? <a hreh="#">You can start here now</a></div>');
//     }, 3000);
//     
//   };
//   
//   window.updateScope = function () {
//     $scope.$apply();
//   };
//   
// }














