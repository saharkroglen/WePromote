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


@ParseClassName("TrustedUser")
public class TrustedUser extends ParseObject{
	private static final String OWNER_EMAIL = "owner_email";
	private static final String TRUSTED_EMAIL = "trusted_email";
	private static final String IS_CONFIRMED = "isConfirmed";
	private static final String IS_REQUESTED_BY_ME = "isRequestedByMe";
	private static String TAG = "User";
	private User mUser;
	
	public TrustedUser()
	{
		
	}
	
	public void saveObject()
	{
		validate();
		setPermissions();	
		
		checkIfTrustedUserAlreadyExistsBeforeSave();
	}
	
	private void validate() {
		if (getOwnerEmail() == null)
			throw new IllegalStateException("TrustedUser not initialized properly. Call 'setOwnerUser(user)' method");
		if ( getTrustedEmail() == null)
			throw new IllegalStateException("TrustedUser not initialized properly. Call 'setTrustedAccount(user)' method");
	}

	
	private void checkIfTrustedUserAlreadyExistsBeforeSave() {
		ParseQuery<TrustedUser> query = ParseQuery.getQuery(TrustedUser.class);
		query.whereEqualTo(TrustedUser.OWNER_EMAIL, getOwnerEmail());
		query.whereEqualTo(TrustedUser.TRUSTED_EMAIL, getTrustedEmail());
		query.findInBackground(new FindCallback<TrustedUser>() {
		    public void done(List<TrustedUser> trustedUsers, ParseException e) {
		        if (e == null) {
		        	if (trustedUsers.size()>0)
		        	{
		        		Log.d(TAG, String.format("Trusted user %s already exists, change status to confirmed",getTrustedEmail()));
		        		trustedUsers.get(0).setIsConfirmed(true);
		        		trustedUsers.get(0).saveInBackground();
		        	}
		        	else
		        	{
		        		Log.d(TAG, String.format("Created new Trusted user %s",getTrustedEmail()));
		        		
		        		saveInBackground();
		        	}
		            
		        } else {
		            Log.d(TAG, "Error: " + e.getMessage());
		        }
		    }
		});
	}
	
	private void setPermissions() {
		//set access permissions private
		ParseACL accessControl = new ParseACL(mUser.getParseUser());		
		setACL(accessControl);
	}
	
	public void setOwnerUser(User user)
	{
		mUser = user;
		if (mUser != null)
		{			
			setOwnerEmail(mUser.getUsername());			
		}
	}
	
	public String getOwnerEmail()
	{
		return getString(OWNER_EMAIL);
	}	
	
	private void setOwnerEmail(String value) {
	    put(OWNER_EMAIL, value);
	}
	
	public String getTrustedEmail()
	{
		return getString(TRUSTED_EMAIL);
	}	
	public void setTrustedUserEmail(String value) 
	{
	    put(TRUSTED_EMAIL, value);
	}
	
	public boolean isConfirmed()
	{
		return getBoolean(IS_CONFIRMED);
	}	
	public void setIsConfirmed(boolean value) {
	    put(IS_CONFIRMED, value);
	}
	
	public boolean isRequestedByMe()
	{
		return getBoolean(IS_REQUESTED_BY_ME);
	}	
	public void setIsRequestedByMe(boolean value) {
	    put(IS_REQUESTED_BY_ME, value);
	}
	
}
