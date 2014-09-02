package com.wepromote.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;
import com.wepromote.adapters.PostAdapter;
import com.wepromote.common.FacebookProvider;
import com.wepromote.common.Utils;
import com.wepromote.dialogs.ShareDialog;
import com.wepromote.parse.Campaign;
import com.wepromote.parse.CampaignPromoter;
import com.wepromote.parse.CampaignPromoter.enInvitationStatus;

/**
 * A placeholder fragment containing a simple view.
 */
public class PostFragment extends Fragment implements ShareDialog.OnShareSelectedListener{
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	// private static final String ARG_SECTION_NUMBER = "section_number";
	public static final String ARG_PROFILE_NAME = "profile_name";
	private PostAdapter mAdapter;
	private GridView mGrid;
	FacebookProvider mFacebookProvider;
	private Context mContext;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */

	public PostFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mFacebookProvider = new FacebookProvider(getActivity());
		mFacebookProvider.onCreate(savedInstanceState);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		mFacebookProvider.onResume();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// String selectedProfileName = getSelectedProfileName();
		final View rootView = inflater.inflate(R.layout.fragment_post,
				container, false);

		//Utils.fillPromoterIDHeader(rootView,null);
		Utils.setCustomFontToViewGroup(rootView,Utils.FONT_OSWALD_REGULAR);
		Utils.showSpinner(WePromoteApplication.getContext(),true);

		mGrid = (GridView) rootView
				.findViewById(R.id.gridAddedPackages);
		mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				
				String campaignID = mAdapter.getCampaignPromoterObjectId(position); 

				new ShareDialog(PostFragment.this,PostFragment.this, R.style.CustomAlertDialog,campaignID).show();
			}
		});
		
		CampaignPromoter.getCampaignIDsByInvitationStatus(WePromoteApplication
				.getUser().getPromoterID(false), enInvitationStatus.accept,
				new CampaignPromoter.OnDone() {

					@Override
					public void done(ArrayList<String> campaignIDs,
							final List<CampaignPromoter> campaignPromoterList) {

						if (campaignIDs.size() == 0) {
							Utils.showToast(PostFragment.this.getActivity(),
									"You have no campaigns to share yet");
							Utils.showSpinner(WePromoteApplication.getContext(),false);
							return;
						}
						Campaign.getCampaigns(campaignIDs,
								new Campaign.OnDone() {

									@Override
									public void done(List<Campaign> campaignList) {
										mAdapter = new PostAdapter(
												PostFragment.this,
												campaignList,campaignPromoterList, mFacebookProvider);
										
										mGrid.setAdapter(mAdapter);
										
										Utils.showSpinner(WePromoteApplication.getContext(),false);
									}
								});
					}

				});
		// Utils.fillPromoterIDHeader(rootView);
		if (mContext != null)
			mContext = container.getContext();
		return rootView;
	}

	// private String getSelectedProfileName() {
	// return getArguments().getString(ARG_PROFILE_NAME);
	// }

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// ((MainActivity)
		// activity).onFragmentAttached(getArguments().getString(
		// ARG_PROFILE_NAME));
	}

	@Override
	public void onPause() {
		super.onPause();
		mFacebookProvider.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mFacebookProvider.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		mFacebookProvider.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebookProvider.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onShareMethodSelected(int color) {
		// TODO Auto-generated method stub
		
	}

//	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
//		@Override
//		public void onError(FacebookDialog.PendingCall pendingCall,
//				Exception error, Bundle data) {
//			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
//		}
//
//		@Override
//		public void onComplete(FacebookDialog.PendingCall pendingCall,
//				Bundle data) {
//			Log.d("HelloFacebook", "Success!");
//		}
//	};

}
