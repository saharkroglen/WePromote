package com.wepromote.fragments;


import java.util.List;

import android.annotation.TargetApi;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.wepromote.MainActivity;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.Utils;

public class HomeFragment extends Fragment implements OnClickListener{

	
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_PROFILE_NAME = "profile_name";
	private ViewGroup mTileShare;
	private Activity mContext;
	private TextView txtCampaignMembership;
	private TextView txtInvitations;
	private TextView txtBenefits;
	private TextView txtShare;
	private TextView txtPreferences;
	private TextView txtTellFriend;
	private ImageView mLogo;
	private ViewGroup mTileCampaignMembership;
	private ViewGroup mainLayout;
	public HomeFragment () {
	
	}
	
	@Override
	public void onDetach() {
	    super.onDetach();

//	    try {
//	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//	        childFragmentManager.setAccessible(true);
//	        childFragmentManager.set(this, null);
//
//	    } catch (NoSuchFieldException e) {
//	        throw new RuntimeException(e);
//	    } catch (IllegalAccessException e) {
//	        throw new RuntimeException(e);
//	    }
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
		
		Utils.setCustomFontToViewGroup(rootView,Utils.FONT_OSWALD_REGULAR);
		
		txtCampaignMembership = (TextView)rootView.findViewById(R.id.txtCampaignMembership);
		txtInvitations = (TextView)rootView.findViewById(R.id.txtInvitations);
		txtBenefits = (TextView)rootView.findViewById(R.id.txtBenefits);
		txtShare = (TextView)rootView.findViewById(R.id.txtShare);
		txtPreferences = (TextView)rootView.findViewById(R.id.txtPreferences);
		txtTellFriend = (TextView)rootView.findViewById(R.id.txtTellFriend);
		
		mTileShare = (ViewGroup)rootView.findViewById(R.id.tileShare);
		mTileShare.setOnClickListener(this);
		
		mTileShare = (ViewGroup)rootView.findViewById(R.id.tileInvitations);
		mTileShare.setOnClickListener(this);
		
		mTileCampaignMembership = (ViewGroup)rootView.findViewById(R.id.tileMembership);
		mTileCampaignMembership.setOnClickListener(this);
//		mGrid = (GridView) rootView.findViewById(R.id.gridAddedPackages);
		
		mLogo = (ImageView)rootView.findViewById(R.id.imgLogoPlaceholder);
		
		mainLayout = (ViewGroup)rootView.findViewById(R.id.mainLayout);
		new Handler(Looper.getMainLooper()).post( new Runnable() {
			public void run() {
				Utils.slideToLeft(mLogo, View.VISIBLE,1500,true,WePromoteApplication.getScreenDimensions().x,0);
			}
		});
//		mainLayout = (ViewGroup)rootView.findViewById(R.id.mainLayout);
//		new Handler(Looper.getMainLooper()).postDelayed( new Runnable() {
//			public void run() {
//				Utils.slideToRight(mainLayout , View.VISIBLE,2000,true,-WePromoteApplication.getScreenDimensions().x,0);
//			}
//		},500);

		
		return rootView;
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
//		((MainActivity) activity).onFragmentAttached(getArguments().getString(
//				ARG_PROFILE_NAME));
		
		
//		Animation anim=new Animation(){
//		    protected void applyTransformation(float interpolatedTime, Transformation t) {
//		        super.applyTransformation(interpolatedTime, t);
//		        // Do relevant calculations here using the interpolatedTime that runs from 0 to 1
//		        v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(30*interpolatedTime)));
//		    }};
//		anim.setDuration(500);
//		v.startAnimation(anim);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.tileShare:
			((MainActivity) mContext).openPost();
			break;
		case R.id.tileInvitations:
			((MainActivity) mContext).openInvitations();
			break;
		case R.id.tileMembership:
//			Utils.slideToLeft(mLogo, View.VISIBLE,2000,true,WePromoteApplication.getScreenDimensions().x,0);
			ParseObject c = new ParseObject("campaign1");
			c.put("name", "hilton");
			c.put("merchantID",WePromoteApplication.getUser().getParseUser());
			try {
				c.save();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("User");
			//innerQuery.whereEqualTo("email", "yosskoss@gmail.com");
			innerQuery.whereExists("password");
			
						
			ParseQuery<ParseObject> query = ParseQuery.getQuery("campaign1");
//			query.orderByDescending("createdAt");
//			query.setLimit(10);
			query.include("User");
			//query.whereExists("merchantID");
			query.whereMatchesQuery("merchantID", innerQuery);
			query.findInBackground(new FindCallback<ParseObject>() {
				  public void done(List<ParseObject> commentList, ParseException e) {
				    // commentList now contains the last ten comments, and the "post"
				    // field has been populated. For example:
				    for (ParseObject comment : commentList) {
				      // This does not require a network access.
				      ParseObject user = comment.getParseObject("merchantID");
				      String email = user.getString("email");
				      Log.d("user", "retrieved a related post");
				    }
				  }
				});			
			break;
		}		
	}
}
