package com.wepromote.parse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseClassName;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.Constants;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;

@ParseClassName("User")
public class User  {
	
	private static final String FACEBOOK_PERMISSION_PUBLISH_ACTIONS = "publish_actions";

	private static final String PROMOTER_ID_FIELD = "promoterID";

//	public interface onParseLoginStateChangeListener {
//        void onParseLoginStateChange(User user);
//    }
//    private onParseLoginStateChangeListener mLoginStateChangeListener;
//    
//    public interface onParseLoginFailureListener {
//        void onParseLoginFailure(String message,boolean isFacebookLogin);
//    }
//    private onParseLoginFailureListener mLoginFailureListener;
    
	private static final String FACEBOOK_USERS_EMAIL_PREFS = "facebookUsersEmail";
	private ParseUser mUser;
	private String mUserName;
	private String mPassword;
	private boolean mIsEmailVerified;
	public static String TAG = "User";
	public boolean mIsFacebookUser = false;

	
	public User()
	{		
		attemptLogin();
		
	}
		
//	public void setOnParseLoginSucceedListener(onParseLoginStateChangeListener listener)
//	{
//		mLoginStateChangeListener = listener;
//	}
//	public void setOnParseLoginFailureListener(onParseLoginFailureListener listener)
//	{
//		mLoginFailureListener = listener;
//	}
	public void checkTrustRequests()
	{
		
		ParseQuery<TrustRequest> query = ParseQuery.getQuery(TrustRequest.class);				
		query.whereEqualTo(TrustRequest.TO, mUser.getUsername());
		query.findInBackground(new FindCallback<TrustRequest>() {
		    public void done(List<TrustRequest> trustReq, ParseException e) {
		    	
		        if (e == null) {
		        	if (trustReq.size()>0)
		        	{
	        			Log.d(TAG, String.format("request arrived from %s", trustReq.get(0).getRequestFrom()));
	        			confirmTrustRequest(trustReq.get(0));
		        	}
	        			
		        } else {
		            Log.d(TAG, "Error: " + e.getMessage());
		        }
		    }			
		});		
	}
	
	public boolean isFacebookUser()
	{
		return mIsFacebookUser;
	}
	
	public boolean isRegularUser()
	{
		return !mIsFacebookUser;
	}
	public void setIsFacebookUser (boolean isFacebook)
	{
		mIsFacebookUser = isFacebook;
	}
	
	public static ParseUser findUserByEmail(final String email)
	{
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("username", email);
		try {
			List<ParseUser> users = query.find();
			if (users.size()>0)
			{
				Log.v(TAG, String.format("found user by email %s",users.get(0).getUsername()));
				return users.get(0);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static void resetPassword(String email)
	{
		ParseUser.requestPasswordResetInBackground(email,
            new RequestPasswordResetCallback() {
				public void done(ParseException e) {
				if (e == null) {
					Log.v(TAG, "Reset password succeeded - An email was successfully sent with reset instructions");
				} else {
					Log.v(TAG, String.format("Reset password failed: %s",e.getMessage()));
				}
			}
		});
	}
	
	public static void findUserByEmailAsync(final String email)
	{
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("username", email);
		query.findInBackground(new FindCallback<ParseUser>() {		 
			@Override
			public void done(List<ParseUser> objects, ParseException e) {
			 	if (e == null) {
			 		Log.v(TAG, String.format("found user by email %s",objects.get(0).getUsername()));
			 		
			    } else {
			    	Log.v(TAG, String.format("Query user failed: %s",e.getMessage()));
			    }				
			}
		});
	}
	
	
	public void refreshUser()
	{
		if (isLoggedIn())
		{
			Log.v(TAG,"start refreshing parse user");
			mUser.refreshInBackground(new RefreshCallback() {
				
				@Override
				public void done(ParseObject arg0, ParseException e) {
					if(e!=null)
					{
						Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_LOGIN_FAILURE, e.getMessage()));
					}	
					else
					{
						checkEmailVerified();
						fireOnLoginStateChange();
					}
					Log.v(TAG,"end refreshing parse user");
				}
			});
		}
	}
	public void attemptLogin()
	{		
		mUser = ParseUser.getCurrentUser();
		
		if (isLoggedIn())
		{			
			String userEmail = checkFacebookUser(mUser.getUsername());			
			if (userEmail != null)
			{
				setIsFacebookUser(true);
				mUser.setEmail(userEmail);				
			}
			WePromoteApplication.setInstallationParams(getPromoterID(false),isFacebookUser());
			checkEmailVerified();
			
			Log.v(TAG, String.format("Logged in as %s ",mUser.getUsername()));
			fireOnLoginStateChange();
		}
		else
			Log.v(TAG, String.format("Logged out"));
	}
	
	public static String checkFacebookUser(String username)
	{		
	    SharedPreferences facebookUsers = WePromoteApplication.getContext().getSharedPreferences(FACEBOOK_USERS_EMAIL_PREFS, Context.MODE_PRIVATE);
	    String userEmail = facebookUsers.getString(username, null);
	    if (userEmail == null || userEmail.equals("null") || userEmail.trim().length() == 0)
	    	return null;
	    else
	    	return userEmail;
	}
	
	public void login(String userName, String pass)
	{
		if (userName == null)
		{			
			return;
		}
		Log.v(TAG, String.format("Login %s", userName));
		setIsFacebookUser(false);
		mUserName = userName;
		mPassword = pass;
		
		ParseUser.logInInBackground(userName,pass, new LogInCallback() {
		  	public void done(ParseUser user, ParseException e) {
			    if (user != null) {
			    	mUser = user;
			    	Log.v(TAG, String.format("Login succeeded: %s", user.getUsername()));
			    	checkEmailVerified();
			    	WePromoteApplication.setInstallationParams(getPromoterID(false),isFacebookUser());
			    	Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_LOGIN_SUCCESSFUL, null));
			    	fireOnLoginStateChange();
			    } 
			    else {
			    	Log.e(TAG, String.format("login failed with error: %s", e.getMessage()));
					Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_LOGIN_FAILURE, e.getMessage()));
					Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
			    }
		  	}				
		});
	}
	
	
	
	public void loginFacebook(final Activity context)
	{
		List<String> permissions = Arrays.asList("basic_info", 
				//"public_profile" - public_profile should replace basic_info  when facebook app is not installed. but we prevent this since other stuff won't work if using the web login for facebook				
				"user_friends",
				"user_about_me", 
				"user_relationships", 
				"user_birthday",
				"user_location",				
				"email");
		
		ParseFacebookUtils.logIn(permissions,context, new LogInCallback() {
			  @Override
			  public void done(final ParseUser facebookUser, ParseException e) {
				if (e != null)
				{
					Log.e(TAG,e.getMessage());
					Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_FACEBOOK_LOGIN_FAILURE, e.getMessage()));
					Utils.showSpinner(WePromoteApplication.getContext(),false);
					return;
				}
				setIsFacebookUser(true);
				Session facebookSession = ParseFacebookUtils.getSession();	
				requestFacebookPublishPermissions(context,facebookSession);
			    if (facebookUser == null) {
			    	Log.d(TAG, "facebookUser returned null. might be due to cancellation of Facebook login by user.");
			    } else if (facebookUser.isNew()) {
				      Log.d(TAG, "User signed up and logged in through Facebook!");
				      mUser = facebookUser;
				      //requestFacebookEmailPermissions(context, facebookSession);
				      getFacebookEmail();
					  Log.d(TAG,String.format("User logged in via Facebook!, resolving email in progress..."));
				      fireOnLoginStateChange();
			    } else {
			    	
			    	mUser = facebookUser;
			    	
			    	String userEmail = checkFacebookUser(mUser.getUsername());
					if (userEmail == null)
					{
							    	
						if (facebookSession != null) {
							//requestFacebookEmailPermissions(context, facebookSession);
							getFacebookEmail();
							Log.d(TAG,String.format("User logged in via Facebook!, resolving email in progress..."));
						}						
					}
					else //already resolved facebook email
					{
						mUser.setEmail(userEmail);
						Log.d(TAG,String.format("User '%s' logged in via Facebook!",facebookUser.getEmail()));
					}
			    	
				  Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_LOGIN_SUCCESSFUL, null));
			      fireOnLoginStateChange();
			      
			    }
			  }

			
			});		
	}

	private void fireOnLoginStateChange() {
		Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_LOGIN_STATE_CHANGE, null));
	}
	private void getFacebookEmail() {
		String fqlQuery = "SELECT email FROM user WHERE uid = me() ";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Session session = Session.getActiveSession();
		
		Request request = new Request(session,"/fql",params,HttpMethod.GET,onFacebookPersonalInfoReceive); 
		Request.executeBatchAsync(request);
	}
//	private void requestFacebookEmailPermissions(final Activity context,Session facebookSession) {
//		// Check for  permissions for email info    
//		if (! facebookSession.getPermissions().contains("email")) { 
//		        List permissions = Arrays.asList("email"); //"publish_actions"
//		    Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(context, permissions);                   
//		    facebookSession.requestNewPublishPermissions(newPermissionsRequest);
//		}
//	} 
	
	public boolean hasFacebookPublishPermission(Session facebookSession)
	{
		if (isFacebookUser())
		{
			return facebookSession.getPermissions().contains(FACEBOOK_PERMISSION_PUBLISH_ACTIONS);
		}
		else
			return false;
	}

	private void requestFacebookPublishPermissions(final Activity context,
			Session facebookSession) {
		// Check for permissions for email info
		if (!hasFacebookPublishPermission(facebookSession)) {
			List permissions = Arrays
					.asList(FACEBOOK_PERMISSION_PUBLISH_ACTIONS); // "publish_stream?"
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
					context, permissions);
			facebookSession.requestNewPublishPermissions(newPermissionsRequest);
		}
	}

	private Request.Callback onFacebookPersonalInfoReceive = new Request.Callback(){         
        public void onCompleted(Response response) {
            Log.i(Constants.TAG, "Result: " + response.toString());
            try
            {
                GraphObject go  = response.getGraphObject();
                JSONObject  jso = go.getInnerJSONObject();
                JSONArray   arr = jso.getJSONArray( "data" );

                for ( int i = 0; i < ( arr.length() ); i++ )
                {
                    JSONObject json_obj = arr.getJSONObject( i );

                    //String id     = json_obj.getString( "uid"           );
                    //String name   = json_obj.getString( "name"          );
                    //String urlImg = json_obj.getString( "pic_square"    );
                    String email = json_obj.getString( "email");
                    if (email != null && email.trim().length()>0)
                    {
                    	mUser.setEmail(email);
                    	saveFacebookEmail(email);                	     
                    	WePromoteApplication.setInstallationParams(getPromoterID(false),isFacebookUser());
//                    	fireOnLoginStateChange();
                    	Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_FACEBOOK_EMAIL_RESOLVED, null));
                    }
                }
            }
            catch ( Throwable t )
            {
                t.printStackTrace();
            }							                
        }

		private void saveFacebookEmail(String email) {
			mUser.setEmail(email);
			mUser.saveInBackground();
			
			SharedPreferences facebookUsersPrefs = WePromoteApplication.getContext().getSharedPreferences(FACEBOOK_USERS_EMAIL_PREFS, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor;
			editor = facebookUsersPrefs.edit();
			editor.putString(mUser.getUsername(), email);
			editor.commit();
			Log.d(TAG,String.format("Resolved user email via facebook. %s",email));
		}                  
	};
	
	public ParseUser getParseUser()
	{
		return mUser;
	}
	public String getUsername()
	{
		if (mUser == null)
			return null;
		if (isFacebookUser())
			return mUser.getEmail();
		else
			return mUser.getUsername();
	}
	public void sendTrustRequest(String emailTo)
	{		
		createTrustedUser(emailTo,false,true);
		
		createTrustRequest(emailTo,false);		
	}

	private void createTrustRequest(String emailTo, boolean isReply) {
		TrustRequest req = new TrustRequest();
		req.setFrom(this);
		req.setRequestTo(emailTo);
		req.setIsReply(isReply);
		//TODO send email to recepient
		req.saveObject();
	}
	
	private void sendEmail()
	{
		ParseCloud.callFunctionInBackground("hello", new HashMap<String, Object>(), new FunctionCallback<String>() {
		  public void done(String result, ParseException e) {
		    if (e == null) {
		      // result is "Hello world!"
		    }
		    
		    Log.v(Constants.TAG,"cloud hello world result: " + result);
		  }
		});
	}

	private void createTrustedUser(String trustedEmail, boolean isConfirmed, boolean isRequestedByMe) {
		TrustedUser trustedUser = new TrustedUser();
		trustedUser.setIsConfirmed(isConfirmed);
		trustedUser.setOwnerUser(this);
		trustedUser.setTrustedUserEmail(trustedEmail);
		trustedUser.setIsRequestedByMe(isRequestedByMe);
		trustedUser.saveObject();
	}
	private void checkEmailVerified() {
		if (mUser != null)
		{	
			mIsEmailVerified =  mUser.getBoolean("emailVerified"); 
			if (mIsEmailVerified)
			{
				Log.v(TAG, "email verified");
			}
			else
				Log.v(TAG, "email NOT verified");
		}
	}
	
	public boolean isEmailVerified()
	{	
		return mIsEmailVerified;
	}
	
	public void setPromoterID(final String promoterID)
	{
		mUser.put(PROMOTER_ID_FIELD, promoterID);
		mUser.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException arg0) {
				Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_PROMOTER_ID_SAVED,null));
				WePromoteApplication.setInstallationParams(promoterID, isFacebookUser());
			}
		});
	} 
	public String getPromoterID(boolean withPrefix)
	{
		if (mUser == null || mUser.get(PROMOTER_ID_FIELD) == null)
			return null;
		
		String promoterID = "";
		if (withPrefix)
		{
			promoterID = Constants.PROMOTER_PREFIX;
		}
		promoterID += (String) mUser.get(PROMOTER_ID_FIELD);	
		return 	promoterID;
	}
	
	public void signup(String userName, String pass) {
		mUserName = userName;
		mPassword = pass;
		Log.v(TAG, String.format("Start Signup for user: %s ", userName));
		ParseUser user = new ParseUser();
		user.setUsername(userName);
		user.setPassword(pass);
		user.setEmail(userName);
		  
		// other fields can be set just like with ParseObject
//		user.put("phone", "0526650441");
//		user.put("email", userName);
		  
		user.signUpInBackground(new SignUpCallback() {
		  public void done(ParseException e) {
		    if (e == null) {
		    	Log.v(TAG, "signup succeded");
		    	login(mUserName, mPassword);
		    	
		    } else {
		    	Log.v(TAG, "signup failed, " + e.getMessage());
				Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SIGNUP_FAILURE,e.getMessage()));
				Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
		    }
		  }
		});
	}
	
	
	public boolean isLoggedIn()
	{
		mUser = ParseUser.getCurrentUser();
		return mUser != null;
	}
	
	public void logout()
	{
		if (mUser == null)
			return;
		
		mUser.logOut();		
		if (Session.getActiveSession() != null)
			Session.getActiveSession().closeAndClearTokenInformation();
		if (ParseFacebookUtils.getSession() != null)
			ParseFacebookUtils.getSession().closeAndClearTokenInformation();
		mUser = null;//ParseUser.getCurrentUser();
		WePromoteApplication.clearInstallationParams();
		fireOnLoginStateChange();
		Log.v(TAG, "logout");
	}
	
	private void confirmTrustRequest(TrustRequest trustReqToConfirm) {
		createTrustedUser(trustReqToConfirm.getRequestFrom(),true,false);
		if (!trustReqToConfirm.isReply())
			createTrustRequest(trustReqToConfirm.getRequestFrom(),true);
		
		trustReqToConfirm.deleteInBackground();
		
	}
	
	
}

