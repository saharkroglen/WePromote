package com.wepromote.lib;

//import com.example.android.apis.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

public class NfcHandshake extends HandshakeBase {

	public interface NFCListener {
		void onNfcMessage(String msg);
	}
	private NFCListener mNfcListener;
	public void setNfcListener(NFCListener listener)
	{
		mNfcListener = listener;
	}

	
	private NfcAdapter mAdapter;
	private NdefMessage mMessage;

	public NfcHandshake(Activity c) {
		super(c);
		mAdapter = NfcAdapter.getDefaultAdapter(c);

        NfcManager manager = (NfcManager) c.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            Log.v("nfc","nfc is enabled");
        }
        else
        	Log.v("nfc","nfc is disabled, please turn NFC on.");
        
       
	}

	@SuppressLint("NewApi")
	@Override
	public void send(String text) {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){ //version 16 and above
			 mMessage = new NdefMessage(NdefRecord.createUri(text));

	        if (mAdapter != null) {
	            mAdapter.setNdefPushMessage(mMessage, mContext);
	            mNfcListener.onNfcMessage("Tap another Android phone with NFC to push a URL");
	        } else {
	        	mNfcListener.onNfcMessage("This phone is not NFC enabled");            
	            Log.v("nfc","this phone is not nfc enabled");
	        }
		} else{
		    mNfcListener.onNfcMessage("This version of Android doesn't support NFC");
		}
	}

	@Override
	public void receive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCampaignInvitationUri(String campaignID,String merchantName) {
		// TODO Auto-generated method stub
		return null;
	}

}
