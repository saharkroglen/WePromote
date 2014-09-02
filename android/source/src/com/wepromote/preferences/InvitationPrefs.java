package com.wepromote.preferences;

import java.io.IOException;
import java.util.ArrayList;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.InvitationList;
import com.wepromote.common.InvitationList.Invitation;
import com.wepromote.common.Utils;
import android.content.Context;
import android.content.SharedPreferences;

public class InvitationPrefs {

	private final static String PENDING_INVITATIONS = "pending_invitations";
	static InvitationList mInvitationList;
	
	private static void init() {
		if (mInvitationList == null)
			LoadPendingInvitationsFromPrefs();
		
	}

	public static ArrayList<Invitation> getPendingInvitations() {
		init();		
		return mInvitationList.PendingInvitations;
	}

	public static void LoadPendingInvitationsFromPrefs() {
		SharedPreferences pref = WePromoteApplication.getContext()
				.getSharedPreferences(PENDING_INVITATIONS, Context.MODE_PRIVATE);
		
		String json = pref.getString(PENDING_INVITATIONS, null);
		if (json == null)
		{
			mInvitationList = new InvitationList();
			return;
		}
		try {
			mInvitationList = Utils.getMapper().readValue(json,
					InvitationList.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void addPendingInvitation(String campaignID,String merchantName)
	{
		init();
		
		for(Invitation invite:mInvitationList.PendingInvitations)
		{
			if(invite.getCampaignID().equals(campaignID))
			{
				return;
			}
		}	
		
		Invitation invite = new Invitation(campaignID,merchantName);		
		mInvitationList.add(invite);
		SavePendingInvitationsToPrefs();
		
	}
	public static void removePendingInvitation(String campaignID)
	{
		init();
				
		mInvitationList.remove(campaignID);
		SavePendingInvitationsToPrefs();
	}

	public static void SavePendingInvitationsToPrefs() {
		if (mInvitationList == null) {
			return;
		}

		SharedPreferences pref = WePromoteApplication
				.getContext()
				.getSharedPreferences(PENDING_INVITATIONS, Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = pref.edit();

		editor.commit();
		new Thread(new Runnable() {

			@Override
			public void run() {		
				try {
					String json = Utils.getMapper().writeValueAsString(mInvitationList);
					editor.putString(PENDING_INVITATIONS, json);
					editor.commit();
				} catch (JsonGenerationException e) {					
					e.printStackTrace();
				} catch (JsonMappingException e) {										
					e.printStackTrace();
				} catch (IOException e) {					
					e.printStackTrace();
				}

			}
		}).start();

	}
	
	

	
}
