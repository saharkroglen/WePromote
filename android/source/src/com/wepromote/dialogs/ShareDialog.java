package com.wepromote.dialogs;

import com.facebook.android.Util;
import com.wepromote.ImagePickActivity;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;
import com.wepromote.lib.Constants;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
	private ViewGroup btnShareMore;

	public ShareDialog(Fragment context, OnShareSelectedListener listener,
			int theme, String campaignID) {
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

		btnShareMore = (ViewGroup) findViewById(R.id.tileShareMore);
		btnShareMore.setOnClickListener(this);

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
		String message = null;
		String campaignLandingPageLink = String.format("http://wepromote.parseapp.com/campaigns/?cid=%s#/",mCampaignID);
		switch (v.getId()) {
		case R.id.tileShareFacebook:
			// Utils.showSpinner(mContext.getActivity(), true);

			Intent shareToFacebook = new Intent(mContext.getActivity(),
					ImagePickActivity.class);
			shareToFacebook.putExtra("campaignLink", campaignLandingPageLink);
			shareToFacebook.putExtra("merchantName", campaignLandingPageLink);
			mContext.startActivityForResult(shareToFacebook, Constants.REQUEST_CODE_CAPTURE_IMAGE_FOR_FACEBOOK);
			break;
		case R.id.tileShareTwitter:

			break;
		case R.id.tileShareMore:
			Intent shareToOtherApps = new Intent(mContext.getActivity(),
					ImagePickActivity.class);
			shareToOtherApps.putExtra("campaignLink", campaignLandingPageLink);
			shareToOtherApps.putExtra("merchantName", campaignLandingPageLink);
			mContext.startActivityForResult(shareToOtherApps, Constants.REQUEST_CODE_CAPTURE_IMAGE_FOR_OTHERS);
			
			

			break;

		case R.id.tileShareWhatsapp:

			PackageManager pm = mContext.getActivity().getPackageManager();
			try {

				Intent waIntent = new Intent(Intent.ACTION_SEND);
				waIntent.setType("text/plain");
				message = String
						.format("Hi Man!\nI became a club member of %s and would like to share with you this benefit %s\n see ya..",
								"Hilton", campaignLandingPageLink);

				PackageInfo info = pm.getPackageInfo("com.whatsapp",PackageManager.GET_META_DATA);
				// Check if package exists or not. If not then code
				// in catch block will be called
				waIntent.setPackage("com.whatsapp");

				waIntent.putExtra(Intent.EXTRA_TEXT, message);
				mContext.getActivity().startActivity(
						Intent.createChooser(waIntent, "Share with"));

			} catch (NameNotFoundException e) {
				Utils.showToast(mContext.getActivity(),
						"WhatsApp not Installed");
			}
			break;
		}
		ShareDialog.this.dismiss();
	}

}
