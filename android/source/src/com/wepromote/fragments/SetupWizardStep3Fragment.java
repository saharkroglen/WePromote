package com.wepromote.fragments;

import android.graphics.Path.FillType;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.wepromote.R;
import com.wepromote.SetupWizardActivity;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.Constants;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;

public class SetupWizardStep3Fragment extends SetupFragmentBase implements OnClickListener {

	Button mBtnPincode, mBtnAddProfile;
	private TextView mTxtPromoterID;
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
        View v = inflater.inflate(R.layout.fragment_wizard_step3, container, false);
        Utils.setCustomFontToViewGroup(v,Utils.FONT_OSWALD_REGULAR);
        TextView desc = (TextView) v.findViewById(R.id.txtStepDesc);
        desc.setText(Html.fromHtml(getResources().getString(R.string.txt_signup_success_message)));
                
//        mTxtPromoterID = (TextView) v.findViewById(R.id.txtPromoterID);
//        mTxtPromoterID.setText(String.format(getString(R.string.txt_signup_success_present_promoter_id), WePromoteApplication.getUser().getPromoterID(true)));
//        
        mBtnPincode = (Button) v.findViewById(R.id.btnDone);
        mBtnPincode.setOnClickListener(this);
        
        Utils.fillPromoterIDHeader(v,getString(R.string.txt_signup_success_present_promoter_id));
       
        
        return v;
    }



	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}
	
	private static SetupWizardStep3Fragment f;
	public static SetupWizardStep3Fragment getInstance(String text, boolean newInstance) {

		if (f != null && !newInstance)
			return f;

        f = new SetupWizardStep3Fragment();
        Bundle b = new Bundle(); 
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btnDone:

//			Message msg = new Message();
//			msg.what = SetupWizardActivity.MESSAGE_SETUP_DONE;	
//			((SetupWizardActivity)getActivity()).sendMessage(msg);
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SETUP_DONE, null));
			break;	
		}
		
	}

	@Override
	public void refresh() {
		Utils.fillPromoterIDHeader(getView(),getString(R.string.txt_signup_success_present_promoter_id));		
	}
}