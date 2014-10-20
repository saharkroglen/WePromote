package com.wepromote.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.wepromote.R;
import com.wepromote.adapters.InvitationsAdapter;
import com.wepromote.common.InvitationList;
import com.wepromote.common.InvitationList.Invitation;
import com.wepromote.lib.Constants;
import com.wepromote.lib.QRHandshake;
import com.wepromote.preferences.InvitationPrefs;

/**
 * A placeholder fragment containing a simple view.
 */
public class InvitationsFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	//private static final String ARG_SECTION_NUMBER = "section_number";
	
	private InvitationsAdapter mAdapter;
	private GridView mGrid;
	private ImageView mImgScanBarcode;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	
	public InvitationsFragment () {
	
	}
	
	@Override
	public void onDetach() {
	    super.onDetach();

	    try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		String selectedProfileName = getSelectedProfileName();
		View rootView = inflater.inflate(R.layout.fragment_invitations, container,
				false);
		
		ArrayList<Invitation> pendingInvitations = collectPendingInvitations();
		
		mImgScanBarcode = (ImageView)rootView.findViewById(R.id.imgScanBarcode);
		mImgScanBarcode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new QRHandshake(getActivity()).receive();
				
			}
		});
		
		
		mAdapter = new InvitationsAdapter(getActivity(), pendingInvitations);
		mGrid = (GridView) rootView.findViewById(R.id.gridAddedPackages);
		mGrid.setAdapter(mAdapter);
		
		//Utils.fillPromoterIDHeader(rootView);

		return rootView;
	}

	private ArrayList<Invitation> collectPendingInvitations() {
		ArrayList<Invitation> pendingInvitations = InvitationPrefs.getPendingInvitations();
		
//		String campaignID = getArguments().getString(Constants.ARG_INVITATION_CAMPAIGN_ID);
//		String merchantName = getArguments().getString(Constants.ARG_INVITATION_CAMPAIGN_NAME);
		
		if (getArguments() != null)
		{
			Invitation invitation = (Invitation) getArguments().getParcelable(Constants.ARG_INVITATION);
			if (invitation != null)
			{	
				if (pendingInvitations == null )
				{
					pendingInvitations = new ArrayList<InvitationList.Invitation>();				
				}
				pendingInvitations.add(invitation);
			}
		}	
		return pendingInvitations;
	}

//	private String getSelectedProfileName() {
//		return getArguments().getString(ARG_PROFILE_NAME);
//	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		((MainActivity) activity).onFragmentAttached(getArguments().getString(
//				ARG_PROFILE_NAME));
	}
}
