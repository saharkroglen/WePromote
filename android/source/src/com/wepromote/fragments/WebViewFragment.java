package com.wepromote.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;

import com.wepromote.MainActivity;
import com.wepromote.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class WebViewFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	//private static final String ARG_SECTION_NUMBER = "section_number";
	public static final String ARG_PROFILE_NAME = "profile_name";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	
	public WebViewFragment () {
	
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String selectedProfileName = getSelectedProfileName();
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
//		TextView textView = (TextView) rootView
//				.findViewById(R.id.section_label);
		WebView mWebView = (WebView) rootView
				.findViewById(R.id.web);
		mWebView.setFocusable(true);
		mWebView.setFocusableInTouchMode(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
//		mWebView.getSettings().setPluginsEnabled(true);
		mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mWebView.getSettings().setDatabaseEnabled(true);
		

		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.v("Sahar", "finished loading url: " + url);
				
				super.onPageFinished(view, url);
			}
		});

		mWebView.loadUrl("google.com");
		return rootView;
	}

	private String getSelectedProfileName() {
		return getArguments().getString(ARG_PROFILE_NAME);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onFragmentAttached(getArguments().getString(
				ARG_PROFILE_NAME));
	}
}
