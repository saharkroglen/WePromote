'use strict';

/* Controllers */

function Dashboard ($scope, $http, Reset) {
  $scope.campaigns = [];
  $scope.campaignsPromoters = [];
  $scope.sharedPosts = 0;
  $scope.likedPosts = 0;
  $scope.selectedCampaign = {};
  var Campaign = Parse.Object.extend("Campaign");
  var query = new Parse.Query(Campaign);
  query.equalTo('MerchentId', Parse.User.current().id);
  query.find({
    success: function(entries) {
      for ( var i = 0; i < entries.length; i++ ){
        entries[i].attributes.campaignId = entries[i].id;
        $scope.campaigns.push(entries[i].attributes);
        Reset.getSocialInfo(entries[i].id).success(function (d) {
          $scope.sharedPosts += d.data[0].share_count;
          $scope.likedPosts += d.data[0].like_count;
        });
        var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
        var query = new Parse.Query(CampaignPromoter);
        query.equalTo('CampaignId', $scope.campaigns[0].campaignId);
        query.find({
          success: function(e) {
            $scope.campaignsPromoters.push(e[0]);
            $scope.$apply();
          }
        });
      }
      $scope.$apply();
    }
  });
  
  
  if (Parse.User.current().attributes.MerchentName) {
    $scope.merchentName = Parse.User.current().attributes.MerchentName;
  }
  else {
    $('#myModal').modal({'show': true});
  }
}

function CampaignOverview ($scope, $http) {
  $scope.campaigns = [];
  $scope.promoters = {};
  var Campaign = Parse.Object.extend("Campaign");
  var query = new Parse.Query(Campaign);
  query.equalTo("MerchentId", Parse.User.current().id);
  query.find({
    success: function(results) {
      $scope.campaigns = results;
      // for (var i = 0; i < $scope.campaigns.length; i++) {
      var i = 0;
      function run () {
        if (i < $scope.campaigns.length) {
          var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
          var query = new Parse.Query(CampaignPromoter);
          query.equalTo("CampaignId", $scope.campaigns[i].id);
          query.find({
            success: function(res) {
              $scope.promoters[$scope.campaigns[i].id] = res;
              i++;
              ($scope.campaigns[i]) ? run () : $scope.$apply();
            }
          });
        }
      }
      run ();
    }
  });
  
  $scope.removeCampaign = function (cid, el) {
    if (confirm("Are you sure?")) {
      var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
      var query = new Parse.Query(CampaignPromoter);
      query.equalTo("CampaignId", cid.id);
      query.find({
        success: function(results) {
          for ( var i = 0; i < results.length; i++ ){
            results[i].destroy();
          }
        }
      });
      var Campaign = Parse.Object.extend("Campaign");
      cid.destroy({
        success: function(myObject) {
          $(el.target).parent().parent().remove();
          $scope.$apply();
        }
      });
    };
  };
}

function AddCampaign ($scope, $http) {
  $scope.deals = [];
  $scope.addNewDeal = function () {
    var deal = {};
    deal.name = $('#dealName').val();
    deal.cost = parseInt($('#dealCost').val());
    deal.value = parseInt($('#dealValue').val());
    deal.discount = parseInt($('#discount').val());
    deal.promoterReward = $scope.promoterReward;
    deal.promoterRewardType = '$';
    $scope.deals.push(deal);
    scrollDown();
  };
  
  $scope.deleteDeal = function (deal, indx) {
    $('#collapse'+indx).on('hidden.bs.collapse', function () {
      for ( var i = 0; i < $scope.deals.length; i++ ) {
        if ($scope.deals[i].$$hashKey == deal.$$hashKey) {
          $scope.deals.splice(i, 1);
          $scope.$apply();
        }
      }
    })
    $('#collapse'+indx).collapse();
  };
  
  $scope.saveDeal = function (deal) {
    for ( var i = 0; i < $scope.deals.length; i++ ) {
      if ($scope.deals[i].$$hashKey == deal.$$hashKey) {
         $scope.deals[i].name = deal.name
         $scope.deals[i].cost = deal.cost
         $scope.deals[i].value = deal.value
         $scope.deals[i].discount = deal.discount
         $scope.deals[i].promoterReward = deal.promoterReward
         $scope.deals[i].promoterRewardType = deal.promoterRewardType      
      }
    }
    alert('Saved');
    scrollDown();
    $('.collapse').collapse()
  };
  
  $scope.submit = function () {
    if ($('#addCampaign')[0].checkValidity()) {
      
      var Campaign = Parse.Object.extend("Campaign");
      var campaign = new Campaign();
      
      var cn = $('#cn').val();
      var af = $('#af').val();
      var at = $('#at').val();
      var rf = $('#rf').val();
      var rt = $('#rf').val();
      var r = ($('#r')[0].checked) ? true : false;
      
      var obj = { campaignName: cn, MerchentId: Parse.User.current().id, ActiveFrom: af, ActiveTo: at, RedeemableFrom: rf, RedeemableTo: rt, Rewardable: r};
      campaign.save(obj, {
        success: function(object) {
          object.set('deals', $scope.deals);
          object.save(null, {
            success: function(object) {
              $('.wrapInnerPage').html('<div class="col-xs-6 col-sm-6 clearfix">\
                                          <span class="visible-xs-block visible-lg-block">✔ Done!</span>\
                                        </div>');
              alert('Campaign Saved!');                          
              location.hash = '/';
            }
          });
        }
      });
    }
  };
}

function EditCampaign ($scope, $http) {
  $scope.campaigns = [];
  $scope.sharedPosts = 0;
  $scope.likedPosts = 0;
  $scope.selectedCampaign = {};
  var Campaign = Parse.Object.extend("Campaign");
  var query = new Parse.Query(Campaign);
  query.equalTo('MerchentId', Parse.User.current().id);
  query.find({
    success: function(entries) {
      for ( var i = 0; i < entries.length; i++ ){
        entries[i].attributes.campaignId = entries[i].id;
        $scope.campaigns.push(entries[i].attributes);
      }
      $scope.$apply();
    }
  });
  
  $scope.changeEvent = function (e) {
    if (e.campaign) {
      for ( var i = 0; i < $scope.campaigns.length; i++ ) {
        if ($scope.campaigns[i].campaignId == e.campaign) {
          $scope.selectedCampaign.name = $scope.campaigns[i].campaignName;
          $scope.selectedCampaign.id = $scope.campaigns[i].campaignId;
          $scope.selectedCampaign.af = $scope.campaigns[i].ActiveFrom;
          $scope.selectedCampaign.at = $scope.campaigns[i].ActiveTo;
          $scope.selectedCampaign.rf = $scope.campaigns[i].RedeemableFrom;
          $scope.selectedCampaign.rt = $scope.campaigns[i].RedeemableTo;
          $scope.selectedCampaign.rewardable = $scope.campaigns[i].Rewardable;
          $scope.selectedCampaign.deals = $scope.campaigns[i].deals;
        }
      }
      $scope.selectedCampaign.show = true;
    }
  };
  
  $scope.deleteDeal = function (deal, indx) {
    $('#collapse'+indx).on('hidden.bs.collapse', function () {
      for ( var i = 0; i < $scope.selectedCampaign.deals.length; i++ ) {
        if ($scope.selectedCampaign.deals[i].$$hashKey == deal.$$hashKey) {
          $scope.selectedCampaign.deals.splice(i, 1);
          $scope.$apply();
        }
      }
    })
    $('#collapse'+indx).collapse();
  };
  
  $scope.saveDeal = function (deal) {
    for ( var i = 0; i < $scope.selectedCampaign.deals.length; i++ ) {
      if ($scope.selectedCampaign.deals[i].$$hashKey == deal.$$hashKey) {
         $scope.selectedCampaign.deals[i].name = deal.name
         $scope.selectedCampaign.deals[i].cost = deal.cost
         $scope.selectedCampaign.deals[i].value = deal.value
         $scope.selectedCampaign.deals[i].discount = deal.discount
         $scope.selectedCampaign.deals[i].promoterReward = deal.promoterReward
         $scope.selectedCampaign.deals[i].promoterRewardType = deal.promoterRewardType      
      }
    }
    alert('Saved');
    scrollDown();
    $('.collapse').collapse()
  };
}

function InvitationOverview ($scope, $http) {
  $scope.campaigns = [];
  $scope.promoters = [];
  $scope.selectedCampaign = {};
  var Campaign = Parse.Object.extend("Campaign");
  var query = new Parse.Query(Campaign);
  query.equalTo('MerchentId', Parse.User.current().id);
  query.find({
    success: function(entries) {
      for ( var i = 0; i < entries.length; i++ ){
        entries[i].attributes.campaignId = entries[i].id;
        $scope.campaigns.push(entries[i].attributes);
      }
      $scope.$apply();
    }
  });
  
  $scope.getStatus = function (s) {
    var status = null;
    switch (s) {
      case 0:
        status = "Pending";
        break;
      case 1:
        status = "Accepted";
        break;
      case 2:
        status = "Declined";
        break;
      case 3:
        status = "Blocked";
        break;
    }
    return status;
  };
  
  $scope.changeEvent = function (e) {
    if (e.campaign) {
      for ( var i = 0; i < $scope.campaigns.length; i++ ) {
        if ($scope.campaigns[i].campaignId == e.campaign) {
          $scope.selectedCampaign.title = $scope.campaigns[i].campaignName;
          $scope.selectedCampaign.id = $scope.campaigns[i].campaignId;
        }
      }
      $scope.selectedCampaign.show = true;
      var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
      var query = new Parse.Query(CampaignPromoter);
      query.equalTo('CampaignId', $scope.selectedCampaign.id);
      query.find({
        success: function(promoters) {
          $scope.promoters = promoters;
          $scope.$apply();
        }
      });
    }
  };
  
  $scope.removePromoter = function (cid, pid, el) {
    if (confirm("Are you sure?")) {
      var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
      var query = new Parse.Query(CampaignPromoter);
      query.equalTo('CampaignId', cid);
      query.equalTo('PromoterId', pid);
      query.find({
        success: function(obj) {
          obj[0].destroy({
            success: function(myObject) {
              $(el.target).parent().parent().remove();
              $scope.$apply();
            }
          });
        }
      });
    };
  };
  
  $scope.updateUi = function (cid, pid, el) {
    var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
    var query = new Parse.Query(CampaignPromoter);
    query.equalTo('CampaignId', cid);
    query.equalTo('PromoterId', pid);
    query.find({
      success: function(obj) {
        $(el.target).parent().parent().parent().find('.status').text($scope.getStatus(obj[0].attributes.Status));
        $scope.$apply();
      }
    });
  };
}

function InvitePromoter ($scope, $http) {
  $scope.users;
  $scope.selectedCampaign = {'show': false};
  $scope.campaigns = [];
  
  var Campaign = Parse.Object.extend("Campaign");
  var query = new Parse.Query(Campaign);
  query.equalTo('MerchentId', Parse.User.current().id);
  query.find({
    success: function(entries) {
      for ( var i = 0; i < entries.length; i++ ){
        entries[i].attributes.campaignId = entries[i].id;
        $scope.campaigns.push(entries[i].attributes);
      }
      $scope.$apply();
    }
  });
  
  $scope.createSubmit = function(){
    $('#invitePromoterForm').submit(function(){
      var query = new Parse.Query(Parse.Installation);
      query.equalTo('promoterID', $('#srch-term').val());
      Parse.Push.send({
        where: query,
        data: {
          action: "action_invite_promoter",
          campaignid: $scope.selectedCampaign.id,
          merchentName: Parse.User.current().attributes.MerchentName
        }
      }, {
        success: function() {
          /////
          var CampaignPromoter = Parse.Object.extend("CampaignPromoter");
          var query = new Parse.Query(CampaignPromoter);
          query.equalTo("CampaignId", $scope.selectedCampaign.id);
          query.find({
            success: function (obj) {
              var isProm = false
              for ( var i = 0; i < obj.length; i++ ) {
                if (obj[i].attributes.PromoterId == $('#srch-term').val())
                  isProm = true
              }
              if (!isProm) {
                var camp = new CampaignPromoter();
                camp.set("CampaignId", $scope.selectedCampaign.id);
                camp.set("PromoterId", $('#srch-term').val());
                camp.set("Status", 0);
                camp.save(null, {
                  success: function(camp) {
                    $('#wrapInnerPage').after('<div class="col-xs-1 col-sm-1 clearfix" id="sentToProm" style="padding-left: 0; width: 265px;"><br/>\
                                            <div class="alert alert-success" role="alert">✔ Invitation was successfully sent!</div>\
                                          </div>');
                    setTimeout(function () {
                      $('#sentToProm').fadeOut(function () {
                        $(this).remove();
                      });
                    }, 2000);
                  }
                });
              }
              else {
                $('#wrapInnerPage').after('<div class="col-xs-1 col-sm-1 clearfix" id="sentToProm" style="padding-left: 0; width: 265px;"><br/>\
                                        <div class="alert alert-success" role="alert">✔ Invitation was successfully sent!</div>\
                                      </div>');
                setTimeout(function () {
                  $('#sentToProm').fadeOut(function () {
                    $(this).remove();
                  });
                }, 2000);
              }
            }
          }); 
        }
      });
    });
  };
  
  $scope.changeEvent = function (e) {
    if (e.campaign) {
      for ( var i = 0; i < $scope.campaigns.length; i++ ) {
        if ($scope.campaigns[i].campaignId == e.campaign) {
          $scope.selectedCampaign.title = $scope.campaigns[i].campaignName;
          $scope.selectedCampaign.id = $scope.campaigns[i].campaignId;
        }
      }
      $scope.selectedCampaign.show = true;
      
      var query = new Parse.Query(Parse.User);
      query.exists('promoterID');
      query.find({
        success: function(users) {
          $scope.users = users;
          $('#promoterID').html('');
          for ( var i = 0; i < $scope.users.length; i++ ) {
              $('#promoterID').append('<option value="'+$scope.users[i].attributes.promoterID+'" data-id="'+$scope.users[i].attributes.id+'"/>')
          }
        }
      });
    }
  };
}

function dialogBox ($scope, $http) {
  $('#updateMNClick').click(function () {
    if ($('#updateMerchrntName').val() != '' || $('#updateMerchrntName').val() != ' ') {
      var user = Parse.User.current();
      user.set("MerchentName", $('#updateMerchrntName').val());
      user.save();
      $('#updateMerchrntName').val('');
      $scope.$apply();
    }
  });
}

function MerchentDisplayName ($scope, $http) {
  $scope.merchentName = null;
  if (Parse.User.current().attributes.MerchentName) {
    $scope.merchentName = Parse.User.current().attributes.MerchentName;
  }
  else {
    $('#myModal').modal({'show': true});
  }
}