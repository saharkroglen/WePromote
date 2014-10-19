package com.wepromote;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.wepromote.common.Utils;
import com.wepromote.lib.Constants;
import com.wepromote.parse.Campaign;
import com.wepromote.parse.CampaignPromoter;
import com.wepromote.parse.PushNotificationActivity;
import com.wepromote.parse.TrustRequest;
import com.wepromote.parse.TrustedUser;
import com.wepromote.parse.User;
import com.wepromote.parse.UserInbox;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;

public class WePromoteApplication extends android.app.Application {

	private static final String FIELD_PROMOTER_ID = "promoterID";
	private static Context sContext;
	private static User mUser;
	public static Point mScreenDimensions; 
	
	@Override
	public void onCreate() {
		super.onCreate();

		sContext = getApplicationContext();

		parseInit();
		facebookIdentityInit();
		initUser();
			
	}
	
	public static Context getContext() {
		return sContext;
	}
	
	
	public static void setScreenDimensions(Point p)
	{
		mScreenDimensions = p;
	}
	public static Point getScreenDimensions()
	{
		return mScreenDimensions;
	}
	private void parseInit() {
		// Add your initialization code here
		Parse.initialize(this, "IwjXCfulcA64BVZd002616T56rLALjuhGnBdIRCn","dJ8piRCUu9Y3fkMAqA1YFDa8yRQEtV0PZv99Q6cV");
		
		registerParseObjects();

		PushService.setDefaultPushCallback(this, PushNotificationActivity.class);
		setInstallationParams(null, false);
		Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

		// ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// defaultACL.setPublicReadAccess(true);// If you would like all objects
		// to be private by default, remove this line.
		ParseACL.setDefaultACL(defaultACL, true); // data is private by default
													// unless said otherwise
	}

	private void registerParseObjects() {
//		ParseObject.registerSubclass(TrustedUser.class);
//		ParseObject.registerSubclass(TrustRequest.class);
//		ParseObject.registerSubclass(UserInbox.class);
		ParseObject.registerSubclass(Campaign.class);
		ParseObject.registerSubclass(CampaignPromoter.class);
	}
	
	public static void setInstallationParams(String promoterID,
			boolean isFacebookUser) {

		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		
		if (promoterID == null) {
			Log.e(Constants.TAG,
					"Can't assign installation. promoterID is null"); 
			return;
		} else {
			installation.put(FIELD_PROMOTER_ID, promoterID);			
		}
		Log.v(Constants.TAG, String.format(
				"Installation set promoterID: %s, facebook user: %s",
				installation.get(FIELD_PROMOTER_ID), isFacebookUser));
		installation.saveInBackground();
	}
	public static void clearInstallationParams() {

		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.remove(FIELD_PROMOTER_ID);					
		Log.d(Constants.TAG, String.format("clear installation params"));
		installation.saveInBackground();
	}
	
	private void facebookIdentityInit() {
		ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
	}

	private void initUser() {
		if (mUser == null)
			mUser = new User();
	}
	
	public static User getUser() {
		return mUser;
	}

}
