package com.wepromote.common;

import java.io.Serializable;
import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;


public class InvitationList implements Serializable{		

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<Invitation>  PendingInvitations = new ArrayList<Invitation>();
	
	public InvitationList()
	{
		
	}
	public int isCampaignExist(String campaignID)
	{
		int i=0;
		for(Invitation invite:PendingInvitations)
		{
			if(invite.getCampaignID().equals(campaignID))
			{
				return i;
			}
			i++;
		}
		return -1;
	}

	public void add(Invitation invite) {
		PendingInvitations.add(invite);
	}

	public void remove(String campaignID) {
		int campaignIndex = isCampaignExist(campaignID);
		if (campaignIndex > -1)
			PendingInvitations.remove(campaignIndex);
		
	}
	
	public static class Invitation implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public String campaignID;
		
		public String merchantName;
		
		public Invitation()
		{
		
		}
		public Invitation(String campaingID, String merchantName)
		{
			this.campaignID =campaingID;
			this.merchantName = merchantName;
		}
//		@Override
//		public int describeContents() {
//			return 0;
//		}
//		@Override
//		public void writeToParcel(Parcel dest, int flags) {		
//			dest.writeString(mCampaignID);		
//			dest.writeString(mMerchantName);
//		}
//		private void readFromParcel(Parcel in) {  
//			mCampaignID = in.readString();
//			mMerchantName = in.readString();
//		}
		
		public String getMerchantName(boolean withPrefix)
		{
			String name = "";
			if (withPrefix)
				name += Constants.MERCHANT_PREFIX  ;
			
			return  name + merchantName;
		}
		public String getCampaignID()
		{
			return campaignID;
		}
	}
}