package com.wepromote.common;

import android.os.Parcel;
import android.os.Parcelable;

public class InternalMessage implements Parcelable{
	
	public final static int MESSAGE_LOGIN_STATE_CHANGE = 1;
	public final static int MESSAGE_PROMOTER_ID_SAVED = 2;
	public final static int MESSAGE_LOGIN_FAILURE = 3;
	public final static int MESSAGE_FACEBOOK_LOGIN_FAILURE = 4;
	public final static int MESSAGE_SIGNUP_FAILURE = 5;
	public final static int MESSAGE_SETUP_DONE = 6;
	public final static int MESSAGE_LOGOUT = 7;
	public final static int MESSAGE_SELECT_MENU_ITEM = 8;
	public final static int MESSAGE_SHOW_SPINNER = 9;
	public final static int MESSAGE_SHOW_SETUP_WIZARD_SPINNER = 10;
	public final static int MESSAGE_FACEBOOK_EMAIL_RESOLVED = 11;
	public final static int MESSAGE_LOGIN_SUCCESSFUL = 12;
	
	
	public int messageID;
	public String messageText;
	public String additionalContent;
	
	public InternalMessage(int messageID, String messageContent)
	{
		this(messageID,messageContent,null);
	}
	public InternalMessage(int messageID, String messageContent,String additionalContent)
	{
		this.messageID = messageID;
		this.messageText = messageContent; 
		this.additionalContent = additionalContent;
	}
//	public InternalMessage(int messageID, Integer messageContent)
//	{
//		this.messageID = messageID;
//		this.messageText = messageContent.toString(); 
//	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeInt(messageID);		
		dest.writeString(messageText);
		dest.writeString(additionalContent);
	}
	private void readFromParcel(Parcel in) {  
		messageID = in.readInt();
		messageText = in.readString();
		additionalContent = in.readString();
	}
}
