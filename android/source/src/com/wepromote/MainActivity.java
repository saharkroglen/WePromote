package com.wepromote;


import com.facebook.widget.FacebookDialog;
import com.wepromote.broadcast_receivers.ParseCustomReceiver;
import com.wepromote.common.Constants;
import com.wepromote.common.DrawerMenuItem;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;
import com.wepromote.fragments.HomeFragment;
import com.wepromote.fragments.PostFragment;
import com.wepromote.fragments.InvitationsFragment;
import com.wepromote.fragments.NavigationDrawerFragment;
import com.wepromote.fragments.WebViewFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.support.v4.widget.DrawerLayout; 

public class MainActivity extends  ActionBarActivity implements 
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	public static final int MENU_ITEM_HOME = 0;
	public static final int MENU_ITEM_INVITATIONS = 1;
	public static final int MENU_ITEM_LOGOUT = 2;
	public static final int MENU_ITEM_PREFS = 3;
	
	public static final int API = android.os.Build.VERSION.SDK_INT;
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private ViewGroup mProgressLayout;
	private TextView mSpinnerText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);		
		
		Utils.setCustomFontToViewGroup(getWindow().getDecorView().getRootView(),Utils.FONT_OSWALD_REGULAR);
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		refreshTitle();
		
		mProgressLayout = (ViewGroup)findViewById(R.id.progressLayout);
		mSpinnerText = (TextView)findViewById(R.id.txtProgressTitle);

		// Set up the drawer.
		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);

		if (!WePromoteApplication.getUser().isLoggedIn()) {
			startSetupWizard();
		} else {
			Log.v(Constants.TAG, "Already logged in");
		}
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver, new IntentFilter(Utils.INTERNAL_MESSAGE_INTENT));
		
		
		String action = this.getIntent().getAction();
		if (action != null && action.equals(ParseCustomReceiver.ACTION_INVITE_PROMOTER))
		{
//			openInvitations();
			//mNavigationDrawerFragment.selectItem(1);
//			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SELECT_MENU_ITEM,String.valueOf(MENU_ITEM_INVITATIONS)));
			openInvitations();
			Utils.dismissNotification(this, Constants.NOTIFICATION_INVITATION_ID);
		}
		else
		{
//			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SELECT_MENU_ITEM,String.valueOf(MENU_ITEM_HOME)));
			openHome();
		}
		
//		Intent i = new Intent(this,ImagePickActivity.class);
//		startActivity(i);
		
		initScreenSize();
	}
	private void initScreenSize() {
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		WePromoteApplication.setScreenDimensions(new Point(width,height));
	}

	

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {			
			handleInternalMessage(Utils.getMessageFromIntent(intent));			
		}
	};
	
	private void handleInternalMessage(InternalMessage msg) {
		switch (msg.messageID) {
		case InternalMessage.MESSAGE_LOGOUT:
			logout();
			break;
//		case InternalMessage.MESSAGE_SELECT_MENU_ITEM:
//			Integer menuID = Integer.valueOf(msg.messageText);
//			if (menuID != null)
//			{
//				mNavigationDrawerFragment.setMenuChecked(menuID,true);
//				handleMenuItemSelection(menuID);
//			}	
//			else
//			{
//				mNavigationDrawerFragment.setMenuChecked(MENU_ITEM_HOME,true);
//				handleMenuItemSelection(MENU_ITEM_HOME);
//			}
//			break;
		case InternalMessage.MESSAGE_SHOW_SPINNER:
			boolean showSpinner = Boolean.valueOf(msg.messageText);
			//setProgressBarIndeterminateVisibility(showSpinner);
			if (showSpinner)
			{
				if (msg.additionalContent != null)
				{
					mSpinnerText.setText(msg.additionalContent);
				}
				else
				{
					mSpinnerText.setText("Loading...");	
				}
				mProgressLayout.setVisibility(View.VISIBLE);
				Log.v(Constants.TAG, String.format("Show spinner"));
			}
			else
			{
				mProgressLayout.setVisibility(View.GONE);
				Log.v(Constants.TAG, String.format("Hide spinner"));
			}
			break;

		
		}
	}
	private void logout() {
		WePromoteApplication.getUser().logout();
		startSetupWizard();
	}
	private void startSetupWizard() {
		Intent i = new Intent(this, SetupWizardActivity.class);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshTitle();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position,DrawerMenuItem drawerMenuItem) {
		// update the main content by replacing fragments
//		FragmentManager fragmentManager = getSupportFragmentManager();
		
		handleMenuItemSelection(drawerMenuItem.getMenuID());
		//drawerMenuItem.doAction();		
	}

	private void handleMenuItemSelection(final int menuID) {

		switch (menuID) {
		case MENU_ITEM_HOME:
			openHome();
			break;
		case MENU_ITEM_INVITATIONS:
			openInvitations();
			break;
		case MENU_ITEM_LOGOUT:
			logout();
			break;
		}

	}

	public void openInvitations()
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment invitationsFrag = new InvitationsFragment();
		Bundle args = new Bundle();
		args.putString(WebViewFragment.ARG_PROFILE_NAME, "todo");
		invitationsFrag.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.container, invitationsFrag)
				.commitAllowingStateLoss();
		
	}
	
	public void openHome()
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment homeFrag = new HomeFragment(); 
		Bundle args = new Bundle();
		args.putString(WebViewFragment.ARG_PROFILE_NAME, "todo");
		homeFrag.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.container, homeFrag)
				.commitAllowingStateLoss();
	}
	public void openPost()
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment postFrag = new PostFragment();
		Bundle args = new Bundle();
		args.putString(WebViewFragment.ARG_PROFILE_NAME, "todo");
		postFrag.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.container, postFrag)
				.commitAllowingStateLoss();
	}

	private void loadWebViewFragment(FragmentManager fragmentManager) {
		Fragment webViewFrag = new WebViewFragment();
		Bundle args = new Bundle();
		args.putString(WebViewFragment.ARG_PROFILE_NAME, "todo");
		webViewFrag.setArguments(args);

		fragmentManager.beginTransaction().replace(R.id.container, webViewFrag)
				.commitAllowingStateLoss();
	}

	public void onFragmentAttached(String profileName) {
		refreshTitle();
	}
	private void refreshTitle() {
		mTitle = getActivityTitle();
	}

	public String getActivityTitle() {
		String title;
		if (WePromoteApplication.getUser().isLoggedIn()) {
			title = WePromoteApplication.getUser().getPromoterID(true);
		} else {
			title = getResources().getString(R.string.app_name);
		}
		return title;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
		if (f instanceof HomeFragment) 
		{
		    finish();
		}
		else
		{
			Utils.showSpinner(WePromoteApplication.getContext(),false);
			openHome();
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
//		System.gc();		
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
		if (f instanceof PostFragment) 
		{
		    ((PostFragment)f).onActivityResult(requestCode, resultCode, data);
		}		
//		FacebookProvider mFacebookProvider = new FacebookProvider();
//
//		mFacebookProvider.onActivityResult(requestCode, resultCode, data,dialogCallback);
	}
	 private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.d("HelloFacebook", "Success!");
	        }
	    };
}
