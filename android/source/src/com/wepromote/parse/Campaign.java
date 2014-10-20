package com.wepromote.parse;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.wepromote.lib.Constants;

@ParseClassName("Campaign")
public class Campaign extends ParseObject {

	private static final String COLUMN_CAMPAIGN_NAME = "campaignName";
	private static final String COLUMN_MERCHANT_ID = "MerchentId";
	private static final String COLUMN_ID = "objectId";
	OnDone resultsCallback;
	public interface OnDone {
	    void done(List<Campaign> campaigns);
	    void doneMerchantCampaigns(List<Campaign> campaigns);
	}
	
	public String getName()
	{
		return getString(COLUMN_CAMPAIGN_NAME);
	}
	public String getMerchantID()
	{
		return getString(COLUMN_MERCHANT_ID);
	}
	public String getID()
	{
		return getObjectId();
	}

	void fireCampaignIDsResult(List<Campaign> campaigns) {
		resultsCallback.done(campaigns);
	}
	
//	public static String getMerchantNameByCampaignID(String campaignID)
//	{
//		ParseQuery<Campaign> query = ParseQuery.getQuery(Campaign.class);
//		query.whereEqualTo(COLUMN_ID, campaignID);
//		query.findInBackground(new FindCallback<Campaign>() {
//			@Override
//			public void done(List<Campaign> list, ParseException e) {
//				if (e == null) {					
//					ParseQuery<ParseUser> query = ParseUser.getQuery();		
//					query.whereEqualTo(COLUMN_ID, list.get(0).getMerchantID() );
//					query.findInBackground(new FindCallback<ParseUser>() {
//					});
//				} else {
//					Log.d(Constants.TAG, "Error: " + e.getMessage());
//				}
//			}
//		});
//	}
	public static void getCampaignsForMerchant(String merchantID,final OnDone doneCallback)
	{
		final ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
		
		
		ParseQuery<Campaign> mainQuery = ParseQuery.getQuery(Campaign.class);		
		mainQuery.whereEqualTo(COLUMN_MERCHANT_ID, merchantID);
		mainQuery.findInBackground(new FindCallback<Campaign>()  {
		
			@Override
			public void done(List<Campaign> list, ParseException e) {
				if (e == null) {					
					doneCallback.doneMerchantCampaigns(list);
				} else {
					Log.d(Constants.TAG, "Error: " + e.getMessage());
				}
			}
		});		
	}
	public static void getCampaigns(List<String> campaignIDs,final OnDone doneCallback)
	{
		final ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
		
		if (campaignIDs.size() == 0)
		{
			doneCallback.done(null);
			return ;
		}
		
		
		List<ParseQuery<Campaign>> queries = new ArrayList<ParseQuery<Campaign>>();
		for(String id:campaignIDs)
		{
			ParseQuery<Campaign> query = ParseQuery.getQuery(Campaign.class);
			query.whereEqualTo(COLUMN_ID, id);
			queries.add(query);
		}
		ParseQuery<Campaign> mainQuery = ParseQuery.or(queries);		
		mainQuery.findInBackground(new FindCallback<Campaign>()  {
		
			@Override
			public void done(List<Campaign> list, ParseException e) {
				if (e == null) {					
					doneCallback.done(list);
				} else {
					Log.d(Constants.TAG, "Error: " + e.getMessage());
				}
			}
		});		
	}
//	public static List<String> getCampaignNames(List<String> campaignIDs,final OnDone doneCallback) {
//		final ArrayList<String> campaignNames = new ArrayList<String>();
//		
//		if (campaignIDs.size() == 0)
//		{
//			doneCallback.done(campaignNames);
//			return campaignNames;
//		}
//		
//		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
//		for(String id:campaignIDs)
//		{
//			ParseQuery<ParseObject> query = ParseQuery.getQuery("Campaign");
//			query.whereEqualTo(COLUMN_ID, id);
//			queries.add(query);
//		}
//		
//		ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);		
//		mainQuery.findInBackground(new FindCallback<ParseObject>() {
//
//			@Override
//			public void done(List<ParseObject> list, ParseException e) {
//				if (e == null) {
//					for (ParseObject campaign:list)
//					{
//						campaignNames.add(campaign.getString(COLUMN_CAMPAIGN_NAME));
//					}
//					doneCallback.done(campaignNames);
//				} else {
//					Log.d(Constants.TAG, "Error: " + e.getMessage());
//				}
//			}
//		});
//		return campaignNames;
//	}

	
//	public static void updateInvitationStatus(final enInvitationStatus status,String campaignID) {
//		ParseQuery<ParseObject> query = ParseQuery.getQuery("CampaignPromoter");
////		ParseObject object = ParseObject.createWithoutData("CampaignID", "yuLKGkmaAU");
//		query.whereEqualTo("CampaignId", campaignID);
//		query.findInBackground(new FindCallback<ParseObject>() {
//
//			@Override
//			public void done(List<ParseObject> list, ParseException e) {
//				if (e == null) {
//					for (ParseObject campaignPromoter : list) {						
//						campaignPromoter.put("Status", new Integer(status.ordinal()));
//						campaignPromoter.saveInBackground();
//					}
//				}
//				else {
//		            Log.d(Constants.TAG, "Error: " + e.getMessage());
//		        }
//			}
//		});
//	}
}
