package com.wepromote.parse;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

//public class ParseHelper {
//
//	public final static String PUSH_MESSAGE_ACTION = "com.shoogisoft.action.parse_message";
//	public static void sendMessageToUser(String email)
//	{
//		ParseQuery<ParseInstallation> installationQuery = ParseInstallation.getQuery();
//		
//		ParsePush parsePush = new ParsePush();
//		//parsePush.setExpirationTimeInterval(604800);//week in seconds.				
//		ParseQuery pQuery = ParseInstallation.getQuery(); // <-- Installation query
//		pQuery.whereEqualTo("username", email); 
//		parsePush.setQuery(pQuery);
//		JSONObject data = null;
//		try {
//			data = new JSONObject(String.format("{\"action\": \"%s\",\"name\": \"kooki\",\"lastname\": \"loki\"}",PUSH_MESSAGE_ACTION));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		parsePush.setData(data);
//		parsePush.sendInBackground();
//		
//	}	
//}
