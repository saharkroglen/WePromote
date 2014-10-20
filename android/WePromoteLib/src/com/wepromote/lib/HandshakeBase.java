package com.wepromote.lib;

import android.app.Activity;
import android.content.Context;

public abstract class HandshakeBase {
	public Activity mContext;
	public HandshakeBase(Activity c)
	{
		mContext = c;
	}
	public abstract void send(String text);

	public abstract void receive();
	
	public abstract String getCampaignInvitationUri(String campaignID,String merchantName) ;
}
