package com.wepromote.adapters;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.wepromote.ImagePickActivity;
import com.wepromote.R;
import com.wepromote.common.Constants;
import com.wepromote.common.FacebookProvider;
import com.wepromote.common.Utils;
import com.wepromote.parse.Campaign;
import com.wepromote.parse.CampaignPromoter;

public class PostAdapter extends BaseAdapter{
	private final Fragment mContext;
	private List<Campaign> mCampaignList;
	private FacebookProvider mFacebookProvider;
	private List<CampaignPromoter> mCampaignPromoterList;

	public PostAdapter(Fragment context, List<Campaign> campaignList,List<CampaignPromoter> campaignPromoterList,
			FacebookProvider fbp) {
		super();
		mCampaignPromoterList = campaignPromoterList;
		mCampaignList = campaignList;
		this.mContext = context;
		mFacebookProvider = fbp;
	}

	
	@Override
	public synchronized View getView(int position, View convertView,
			ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			if (position%2 == 0)
				convertView = inflater.inflate(R.layout.item_merchant_post_left_aligned, parent,false);
			else
				convertView = inflater.inflate(R.layout.item_merchant_post_right_aligned, parent,false);
		}

		ViewGroup promoteLine = (ViewGroup) convertView.findViewById(R.id.promote_item);
		
		TextView campaignTextView = (TextView) convertView.findViewById(R.id.txtCampaignName);
		String campaignText = getItem(position).getName();
		campaignTextView.setText(campaignText);
		
		ImageView imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
		ImageView imgBusinessImage = (ImageView) convertView.findViewById(R.id.imgBusinessImage);
		
		setLogoAndBusinessImage(campaignText, imgLogo, imgBusinessImage);
		
		return convertView;

	}


	private void setLogoAndBusinessImage(String campaignText, ImageView imgLogo, ImageView imgBusinessImage) {
//		if (imgBusinessImage == null || imgLogo == null)
//			return;
		if (campaignText.toLowerCase().contains("fox"))
		{
			imgLogo.setImageResource(R.drawable.logo_fox);
			imgBusinessImage.setImageResource(R.drawable.image_fox);
		}
		else if (campaignText.toLowerCase().contains("tatti"))
		{
			imgLogo.setImageResource(R.drawable.logo_tatti);
			imgBusinessImage.setImageResource(R.drawable.image_tatti);
		}
		else if (campaignText.toLowerCase().contains("hilton"))
		{
			imgLogo.setImageResource(R.drawable.logo_hilton);
			imgBusinessImage.setImageResource(R.drawable.image_hilton);
		}
	}

	public String getCampaignPromoterObjectId(int position) {
		return mCampaignPromoterList.get(position).getObjectId();
	}

	@Override
	public int getCount() {
		return mCampaignList.size();
	}

	@Override
	public Campaign getItem(int position) {
		return mCampaignList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}


}
