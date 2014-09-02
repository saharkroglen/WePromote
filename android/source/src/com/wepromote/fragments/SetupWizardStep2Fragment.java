package com.wepromote.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.wepromote.R;
import com.wepromote.SetupWizardActivity;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;
import com.wepromote.parse.User;

public class SetupWizardStep2Fragment extends SetupFragmentBase implements
		OnClickListener {

	Button mBtnChoosePromoterID;
	EditText txtPromoterID;
	private TextView txtTitle;
	private TextView txtSuggested;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wizard_step2, container,
				false);

		Utils.setCustomFontToViewGroup(v,Utils.FONT_OSWALD_REGULAR);
		// TextView desc = (TextView) v.findViewById(R.id.txtStepDesc);
		// desc.setText(Html.fromHtml(getResources().getString(R.string.txt_wizard_step2)));

		txtPromoterID = (EditText) v.findViewById(R.id.txtPromoterID);
		txtTitle = (TextView) v.findViewById(R.id.txtTitle);
		txtSuggested = (TextView) v.findViewById(R.id.txtSuggested);
		mBtnChoosePromoterID = (Button) v.findViewById(R.id.btnChoosePromoterId);
		mBtnChoosePromoterID.setOnClickListener(this);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		//refresh();
	}

	private void suggestPromoterID() {
		String email = WePromoteApplication.getUser().getUsername();
		if (email != null && email.contains("@")) {
			String[] emailSplit = email.split("@");
			String suggestedPromoterID = emailSplit[0];
			txtPromoterID.setText(suggestedPromoterID);
		}
	}

	private static SetupWizardStep2Fragment f;
	public static SetupWizardStep2Fragment getInstance(String text, boolean newInstance) {

		if (f != null && !newInstance)
			return f;


		f = new SetupWizardStep2Fragment();
		Bundle b = new Bundle();
		b.putString("msg", text);

		f.setArguments(b);

		return f;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btnChoosePromoterId:
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"true"));
			String promoterID = txtPromoterID.getText().toString().trim();
			if (WePromoteApplication.getUser().getPromoterID(false) != null && WePromoteApplication.getUser().getPromoterID(false).length()>0)
			{	
				if (WePromoteApplication.getUser().getPromoterID(false)
						.equals(promoterID)) {
//					Message msg = new Message();
//					msg.what = SetupWizardActivity.MESSAGE_PROMOTER_ID_SAVED;
//					((SetupWizardActivity)getActivity()).sendMessage(msg);
					Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_PROMOTER_ID_SAVED, null));
				}
				else
				{
					String currentPromoterID = WePromoteApplication.getUser().getPromoterID(false);
					txtPromoterID.setText(currentPromoterID );
					Utils.showToast(SetupWizardStep2Fragment.this.getActivity(), String.format("User already has a Promoter ID: %s",currentPromoterID));
				}
				
				return;				
			}
			if (promoterID.length()>0)
			{
				WePromoteApplication.getUser().setPromoterID(promoterID);
			}
			break;
		}

	}

	@Override
	public void refresh() {
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				User user = WePromoteApplication.getUser();
				if (user == null)
					return;
				
				String promoterID = user.getPromoterID(false);
				if (promoterID == null) {
					if (txtPromoterID.getText().toString().trim().length()==0)
					{
						suggestPromoterID();
					}
				} else {
					txtPromoterID.setText(promoterID);
					WePromoteApplication.setInstallationParams(promoterID,
							user.isFacebookUser());
				}
				
			}
		});		
	}
}