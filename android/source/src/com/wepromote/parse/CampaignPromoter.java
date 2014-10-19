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

@ParseClassName("CampaignPromoter")
public class CampaignPromoter extends ParseObject{
	private static final String COLUMN_CAMPAIGN_ID = "CampaignId";
	private static final String COLUMN_STATUS = "Status";
	private static final String COLUMN_PROMOTER_ID = "PromoterId";

	public enum enInvitationStatus {
		pending, accept, decline, spam
	}
	
	OnDone resultsCallback;
	public interface OnDone {
	   
		void done(ArrayList<String> campaignIDs,
				List<CampaignPromoter> campaignPromoterList);
	}

	public String getCampaignID()
	{
		return getString(COLUMN_CAMPAIGN_ID);
	}
	
	public void setStatus(enInvitationStatus status)
	{
		put(COLUMN_STATUS, new Integer(status.ordinal()));
	}
	
//	void fireCampaignIDsResult(List<String> campaignIDs) {
//		resultsCallback.done(campaignIDs);
//	}
//	public static CampaignPromoter getCampaignPromoterByCampaign(String campaignID)
//	{
//		
//	}
	public static List<String> getCampaignIDsByInvitationStatus(String promoterID,enInvitationStatus status,final OnDone doneCallback) {
	
		final ArrayList<String> campaignIDs = new ArrayList<String>();
		if (promoterID == null)
			return campaignIDs;
		
		ParseQuery<CampaignPromoter> query = ParseQuery.getQuery(CampaignPromoter.class);		
		query.whereEqualTo(COLUMN_PROMOTER_ID, promoterID);				
		query.whereEqualTo(COLUMN_STATUS, new Integer(status.ordinal()));
		query.findInBackground(new FindCallback<CampaignPromoter>() {

			@Override
			public void done(List<CampaignPromoter> campaignPromoterList, ParseException e) {
				if (e == null) {
					for (CampaignPromoter campaignPromoter:campaignPromoterList)
					{
						campaignIDs.add(campaignPromoter.getCampaignID());
					}
					doneCallback.done(campaignIDs,campaignPromoterList);
				} else {
					Log.d(Constants.TAG, "Error: " + e.getMessage());
				}
			}
		});
		return campaignIDs;
	}

	
	public static void updateInvitationStatus(final enInvitationStatus status,String campaignID) {
		ParseQuery<CampaignPromoter> query = ParseQuery.getQuery(CampaignPromoter.class);
		query.whereEqualTo(COLUMN_CAMPAIGN_ID, campaignID);
		query.findInBackground(new FindCallback<CampaignPromoter>() {

			@Override
			public void done(List<CampaignPromoter> list, ParseException e) {
				if (e == null) {
					for (CampaignPromoter campaignPromoter : list) {						
						campaignPromoter.setStatus(status);
						campaignPromoter.saveInBackground();
					}
				}
				else {
		            Log.d(Constants.TAG, "Error: " + e.getMessage());
		        }
			}
		});
	}
	
	
}
