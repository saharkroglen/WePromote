package com.wepromote.dialogs;

import com.wepromote.ImagePickActivity;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.Constants;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ShareDialog extends Dialog implements
		android.view.View.OnClickListener {

	public interface OnShareSelectedListener {
		void onShareMethodSelected(int color);
	}

	private Fragment mContext;
	private ViewGroup btnShareFacebook;
	private ViewGroup btnShareTwitter;
	private ViewGroup btnShareInstagram;
	private ViewGroup btnShareWhatsapp;
	private String mCampaignID;

	public ShareDialog(Fragment context, OnShareSelectedListener listener,
			int theme,String campaignID) {
		super(context.getActivity(), theme);
		mContext = context;
		mCampaignID = campaignID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_share);

		// final View rootView =
		// LayoutInflater.from(mContext).inflate(R.layout.dialog_share);

		Utils.setCustomFontToViewGroup(this.getWindow().getDecorView()
				.getRootView(), Utils.FONT_OSWALD_REGULAR);
		btnShareFacebook = (ViewGroup) findViewById(R.id.tileShareFacebook);
		btnShareFacebook.setOnClickListener(this);

		btnShareTwitter = (ViewGroup) findViewById(R.id.tileShareTwitter);
		btnShareTwitter.setOnClickListener(this);

		btnShareInstagram = (ViewGroup) findViewById(R.id.tileShareInstagram);
		btnShareInstagram.setOnClickListener(this);

		btnShareWhatsapp = (ViewGroup) findViewById(R.id.tileShareWhatsapp);
		btnShareWhatsapp.setOnClickListener(this);

		ViewGroup btnFacebookPost = (ViewGroup) findViewById(R.id.tileShareFacebook);
		// btnFacebookPost.setTag(getCampaignPromoterObjectId(position));
		btnFacebookPost.setOnClickListener(this);

		ViewGroup btnTwitterPost = (ViewGroup) findViewById(R.id.tileShareTwitter);
		// btnTwitterPost.setTag(getCampaignPromoterObjectId(position));
		btnTwitterPost.setOnClickListener(this);

		ViewGroup btnInstagramPost = (ViewGroup) findViewById(R.id.tileShareInstagram);
		// btnFacebookPost.setTag(getCampaignPromoterObjectId(position));
		btnInstagramPost.setOnClickListener(this);

		ViewGroup btnWhatsappPost = (ViewGroup) findViewById(R.id.tileShareWhatsapp);
		// btnFacebookPost.setTag(getCampaignPromoterObjectId(position));
		btnWhatsappPost.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.tileShareFacebook:
			Utils.showSpinner(mContext.getActivity(), true);
			String campaignLandingPageLink = String.format(
					"http://wepromote.parseapp.com/campaigns/?cid=%s#/",
					mCampaignID);
			Log.v(Constants.TAG, "campaign landing page: "
					+ campaignLandingPageLink);

			Intent i = new Intent(mContext.getActivity(),
					ImagePickActivity.class);
			i.putExtra("campaignLink", campaignLandingPageLink);
			i.putExtra("merchantName", campaignLandingPageLink);
			mContext.startActivityForResult(i, 99);
			break;
		case R.id.tileShareTwitter:

			break;
		}
		ShareDialog.this.dismiss();
	}

}
