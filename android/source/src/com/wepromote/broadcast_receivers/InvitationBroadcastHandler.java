package com.wepromote.broadcast_receivers;

import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.wepromote.MainActivity;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.InvitationList.Invitation;
import com.wepromote.lib.Constants;
import com.wepromote.preferences.InvitationPrefs;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.RemoteViews;

public class InvitationBroadcastHandler  {
	
	public InvitationBroadcastHandler(Context c, String jsonStr) {
//		String jsonStr = json.getStringExtra(ParseCustomReceiver.EXTRA_JSON);
		String  merchantName = null;
		if (jsonStr != null && jsonStr.trim().toString().length()>0)
		{
		    try {
				JSONObject jsonObj = new JSONObject(jsonStr);
				String  campaignID = jsonObj.getString(ParseCustomReceiver.FIELD_CAMPAIGN_ID);
				merchantName = jsonObj.getString(ParseCustomReceiver.FIELD_MERCHANT_NAME);
				InvitationPrefs.addPendingInvitation(campaignID,merchantName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
		RemoteViews contentView = new RemoteViews(WePromoteApplication
				.getContext().getPackageName(),
				R.layout.notification_add_package);

		String notificationMessage;

		List<Invitation> pendingInvitationSet = InvitationPrefs.getPendingInvitations();
		if (pendingInvitationSet.size() == 1)
			notificationMessage = String
					.format(c.getResources().getString(R.string.invitation_by_single_merchant),merchantName);
		else
			notificationMessage = String
					.format(c.getResources().getString(R.string.invitation_by_several_merchants),pendingInvitationSet.size());

		contentView.setTextViewText(R.id.txt_package_added,new SpannableStringBuilder(Html.fromHtml(notificationMessage)));
		contentView.setTextViewText(R.id.txt_package_added_title,c.getResources().getString(R.string.notification_invitation_title));

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(c.getResources().getString(R.string.notification_invitation_title))
				.setContent(contentView);
		// Creates an explicit intent for an Activity in your app
//		Intent i = new Intent(c, MainActivity.class);
//	  	  i.putExtra(ParseCustomReceiver.EXTRA_JSON, jsonStr);
//	  	  i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		  c.startActivity(i);

		Intent resultIntent = new Intent(c,MainActivity.class);
		resultIntent.setAction(ParseCustomReceiver.ACTION_INVITE_PROMOTER);
		resultIntent.putExtra(ParseCustomReceiver.EXTRA_JSON, jsonStr);
			

	
		android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(c);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(Constants.NOTIFICATION_INVITATION_ID, mBuilder.build());
		
	}

}