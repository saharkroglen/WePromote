package com.wepromote.parse;

import java.util.List;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("TrustRequest")
public class TrustRequest extends ParseObject{
	
	public static final String TO = "requestTo";
	public static  final String FROM = "requestFrom";
	public static  final String IS_REPLY = "isReply";
	private static String TAG = "User";
	private User mFrom;
	
	public TrustRequest()
	{
				
	}


	private void setPermissions() {
		//set access permissions read/write to public
		ParseACL accessControl = new ParseACL(mFrom.getParseUser());
		accessControl.setPublicWriteAccess(true);
		accessControl.setPublicReadAccess(true);
		setACL(accessControl);
	}
	
	public void setFrom(User user)
	{
		mFrom = user;

	}
	
	public void saveObject()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				validate();
				setPermissions();
				setRequestFrom(mFrom.getUsername());	
				
				TrustRequest pendingRequest = findSimilarRequest(mFrom.getUsername(),getRequestTo());
				if (pendingRequest == null)
				{
					Log.v(TAG, String.format("New Trust request from: %s to %s",getRequestFrom(),getRequestTo()));
	        		saveInBackground();
				}
				else
				{
					Log.v(TAG, String.format("Trust request from: %s to %s already exists",pendingRequest.getRequestFrom(),pendingRequest.getRequestTo()));
				}				
			}
		}).start();
							
	}


	private void validate() {
		if (mFrom == null)
			throw new IllegalStateException("TrustRequest not initialized properly. Call 'setFrom(user)' method");
		if ( getString(TO) == null)
			throw new IllegalStateException("TrustRequest not initialized properly. Call 'setRequestTo(user)' method");
	}


	public static TrustRequest findSimilarRequest(String from, String to) {
		ParseQuery<TrustRequest> query = ParseQuery.getQuery(TrustRequest.class);
		query.whereEqualTo(TrustRequest.FROM, from);
		query.whereEqualTo(TrustRequest.TO, to);
		try {
			List<TrustRequest> trustReq = query.find();
			if (trustReq.size()>0)
			{
				return trustReq.get(0);
			}
			else
				return null;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		  
	}
	
	public String getRequestTo()
	{
		return getString(TO);
	}	
	public void setRequestTo(String value) {
	    put(TO, value);
	}
	
	public boolean isReply()
	{
		return getBoolean(IS_REPLY);
	}	
	
	public void setIsReply(boolean value) {
	    put(IS_REPLY,value);
	}
	
	public String getRequestFrom()
	{
		return getString(FROM);
	}	
	public void setRequestFrom(String value) 
	{
	    put(FROM, value);
	}	
}
