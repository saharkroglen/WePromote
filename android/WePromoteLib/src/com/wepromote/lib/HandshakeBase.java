package com.wepromote.lib;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

public abstract class HandshakeBase {
	public Activity mContext;
	public HandshakeBase(Activity c)
	{
		mContext = c;
	}
	public abstract void send(String text);

	public abstract void receive();
	
	public abstract String getCampaignInvitationUri(String campaignID,String merchantName) ;
	
	public String getUri(String campaignID,String merchantName)
	{
		return String.format("wepromote://invite/?campaignID=%s&merchantName=%s", TextUtils.htmlEncode(campaignID),TextUtils.htmlEncode(merchantName));
	}
}
