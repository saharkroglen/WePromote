package com.wepromote.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;
import com.wepromote.common.Constants;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;
import com.wepromote.parse.User;

public class SetupWizardStep1Fragment extends SetupFragmentBase implements
		OnClickListener {

	private Button mBtnLoginFacebook;
	private Button mBtnLogin;
	private Button mBtnSignup;
	private EditText txtUsername;
	private EditText txtPassword;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wizard_step1, container,
				false);
		Utils.setCustomFontToViewGroup(v,Utils.FONT_OSWALD_REGULAR);
		mBtnLoginFacebook = (Button)v.findViewById(R.id.btnLoginFacebook);
		mBtnLoginFacebook.setOnClickListener(this);
		
		txtUsername = (EditText) v.findViewById(R.id.txtUsername);		
		txtPassword = (EditText) v.findViewById(R.id.txtPassword);		
		
		mBtnLogin = (Button) v.findViewById(R.id.btnLogin);
		mBtnLogin.setOnClickListener(this);
		
		mBtnSignup = (Button) v.findViewById(R.id.btnSignup);
		mBtnSignup.setOnClickListener(this);
		
		Utils.HideKeyboard(this.getActivity(), txtUsername);
		
		return v;
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	private static SetupWizardStep1Fragment f;
	public static SetupWizardStep1Fragment getInstance(String text, boolean newInstance) {

		if (f != null && !newInstance)
			return f;
		
		f = new SetupWizardStep1Fragment();
		Bundle b = new Bundle();
		b.putString("msg", text);

		f.setArguments(b);

		return f;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnLoginFacebook:			
			facebookLogin();
			break;
		case R.id.btnSignup:
			signup();
			break;
		case R.id.btnLogin:
			login();
			break;
		}
	}

	private void facebookLogin() {
		if (Utils.isFacebookAppInstalled())
		{
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"true"));
			WePromoteApplication.getUser().loginFacebook(SetupWizardStep1Fragment.this.getActivity());
		}
		else
		{
			Utils.showToast(this.getActivity(), R.string.toast_facebook_app_missing);
		}
	}
	
	
	private void signup() {
		
		User user = WePromoteApplication.getUser();
		if (user.isLoggedIn() && user.isRegularUser())
			user.logout();
		else if (user.isLoggedIn() && user.isFacebookUser())
		{
			Log.v(Constants.TAG, String.format("Already logged in as %s",user.getUsername()));
			return;
		}
		Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"true"));
		
		String username = txtUsername.getText().toString().trim()
				.toLowerCase();
		String pass = txtPassword.getText().toString().trim();
		if (!Utils.isValidEmail(username)) {
			Utils.showToast(SetupWizardStep1Fragment.this.getActivity(), R.string.toast_invalid_email);
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
			return;
		}
		if (username.length() > 0 && pass.length() > 0) {
			user.signup(username,
					pass);					
		} else {
			Utils.showToast(SetupWizardStep1Fragment.this.getActivity(),R.string.toast_enter_credentials);
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
		}
	}
	
	
	private void login() {
		Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"true"));
		User user = WePromoteApplication.getUser();
		
		user.logout();
		String username = txtUsername.getText().toString().trim()
				.toLowerCase();
		String pass = txtPassword.getText().toString().trim();
		if (!Utils.isValidEmail(username)) {
			Utils.showToast(SetupWizardStep1Fragment.this.getActivity(), R.string.toast_invalid_email);
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
			return;
		}
		if (username.length() > 0 && pass.length() > 0) {
			user.login(username,
					pass);
			
		} else {
			Utils.showToast(SetupWizardStep1Fragment.this.getActivity(),R.string.toast_enter_credentials);
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
		}
	}

	@Override
	public 	void refresh() {
		// TODO Auto-generated method stub
		
	}
}