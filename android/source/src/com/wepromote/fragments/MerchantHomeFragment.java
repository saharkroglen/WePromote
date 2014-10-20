package com.wepromote.fragments;

import java.util.ArrayList;
import java.util.List;

import com.wepromote.adapters.PostAdapter;
import com.wepromote.common.Utils;
import com.wepromote.lib.HandshakeBase;
import com.wepromote.lib.NfcHandshake;
import com.wepromote.lib.NfcHandshake.NFCListener;
import com.wepromote.lib.QRHandshake;
import com.wepromote.parse.Campaign;
import com.wepromote.adapters.CampaignSpinnerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.wepromote.R;
import com.wepromote.WePromoteApplication;

/**
 * A placeholder fragment containing a simple view.
 */
public class MerchantHomeFragment extends Fragment implements OnClickListener {

	private ViewGroup mTileQR;
	private ViewGroup mTileNFC;
	private TextView mTxtTitle;
	private Spinner mCampaignsSpiner;
	private CampaignSpinnerAdapter mCampaignSpinnerAdapter;

	public MerchantHomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_merchant_home,
				container, false);

		mCampaignsSpiner = (Spinner) rootView
				.findViewById(R.id.spinnerCampaigns);

		Campaign.getCampaignsForMerchant(WePromoteApplication.getUser()
				.getUserID(), new Campaign.OnDone() {

			@Override
			public void doneMerchantCampaigns(final List<Campaign> campaigns) {
				MerchantHomeFragment.this.getActivity().runOnUiThread(
						new Runnable() {
							@Override
							public void run() {
								mCampaignSpinnerAdapter = new CampaignSpinnerAdapter(
										MerchantHomeFragment.this.getActivity(),
										(ArrayList<Campaign>) campaigns);
								mCampaignsSpiner
										.setAdapter(mCampaignSpinnerAdapter);
							}
						});
			}

			@Override
			public void done(final List<Campaign> campaigns) {

			}
		});

		mTxtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
		mTxtTitle.setText(String.format("'%s' Dashboard", WePromoteApplication
				.getUser().getMerchantName()));

		mTileQR = (ViewGroup) rootView.findViewById(R.id.tileQR);
		mTileQR.setOnClickListener(this);

		mTileNFC = (ViewGroup) rootView.findViewById(R.id.tileNFC);
		mTileNFC.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View v) {
		String campaignID = mCampaignSpinnerAdapter.getCampaign(
				mCampaignsSpiner.getSelectedItemPosition()).getID();
		HandshakeBase handshake ;
		switch (v.getId()) {
		case R.id.tileNFC:
			NfcHandshake nfcHandshake = new NfcHandshake(getActivity());
			nfcHandshake.setNfcListener(new NFCListener() {
				
				@Override
				public void onNfcMessage(String msg) {
					Utils.showToast(MerchantHomeFragment.this.getActivity(), msg);
					
				}
			});
			nfcHandshake.send(nfcHandshake.getCampaignInvitationUri(campaignID,
					WePromoteApplication.getUser().getMerchantName()));
			break;
		case R.id.tileQR:
			
			handshake = new QRHandshake(getActivity());
			handshake.send(handshake.getCampaignInvitationUri(campaignID,
					WePromoteApplication.getUser().getMerchantName()));

			break;

		}

	}
}