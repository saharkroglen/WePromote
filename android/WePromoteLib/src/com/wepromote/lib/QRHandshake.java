package com.wepromote.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class QRHandshake extends HandshakeBase {

	public QRHandshake(Activity c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void send(String text) {
//		private void generateBarcode(String content) {
			Intent intent = new Intent("com.google.zxing.client.android.ENCODE");			
			intent.putExtra("ENCODE_FORMAT", "QR_CODE");		
			intent.putExtra("ENCODE_DATA", text);
			intent.putExtra("ENCODE_TYPE", "TEXT_TYPE" );
			intent.putExtra("ENCODE_SHOW_CONTENTS", false );
			mContext.startActivity(intent);
//		}
		
	}

	@Override
	public void receive() {
//		private void scanBarcode() {
			Intent intent = new Intent("com.wepromote.SCAN_QR");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			mContext.startActivityForResult(intent, Constants.REQUEST_CODE_SCAN_QR);
//		}
		
	}

}
