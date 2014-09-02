package com.wepromote;

import com.parse.ParseFacebookUtils;
import com.wepromote.common.Constants;
import com.wepromote.common.InternalMessage;
import com.wepromote.common.Utils;
import com.wepromote.controls.PagingControllableViewPager;
import com.wepromote.fragments.SetupFragmentBase;
import com.wepromote.fragments.SetupWizardStep1Fragment;
import com.wepromote.fragments.SetupWizardStep2Fragment;
import com.wepromote.fragments.SetupWizardStep3Fragment;
import com.wepromote.parse.User;
import com.wepromote.R;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SetupWizardActivity extends FragmentActivity implements
		OnClickListener {

	Button mStep1Cubic, mStep2Cubic, mStep3Cubic;
//	ImageView mHeaderStep;
//	private ImageView mHeaderStepsComplement;
	//private TextView mTextView;
	private ImageView mBtnNext;
	private ImageView mBtnPrev;
	private PagingControllableViewPager mPager;
	private ImageView mBtnCancelWizard;
	private MyPagerAdapter mWizardPagerAdapter;
	private ViewGroup mProgressLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_wizard);
		
		// initHandler();
		mStep1Cubic = (Button) findViewById(R.id.step1Cubic);
		mStep2Cubic = (Button) findViewById(R.id.step2Cubic);
		mStep3Cubic = (Button) findViewById(R.id.step3Cubic);
//		mHeaderStep = (ImageView) findViewById(R.id.stepHeader);
//		mHeaderStepsComplement = (ImageView) findViewById(R.id.stepComplement);
//		mTextView = (TextView) findViewById(R.id.txtStepName);
		mProgressLayout = (ViewGroup)findViewById(R.id.progressLayout);

		initSetupPager();

		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver,
				new IntentFilter(Utils.INTERNAL_MESSAGE_INTENT));
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			handleInternalMessage(Utils.getMessageFromIntent(intent));
		}
	};

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mMessageReceiver);
		super.onDestroy();
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	// public Handler getSetupWizardHandler() {
	// return mHandler;
	// }
	//
	// public void sendMessage(Message msg) {
	// if (mHandler != null)
	// mHandler.sendMessage(msg);
	// }

	// private void initHandler() {
	// if (mHandler != null)
	// return;
	//
	// mHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// if (mWizardPagerAdapter == null || mPager == null)
	// return;
	//
	// handleWizardMessages(msg);
	// }
	//
	// private void handleWizardMessages(Message msg) {
	// String errorMessage;
	// super.handleMessage(msg);
	// handleInternalMessage(msg.what,msg.obj);
	// }
	//
	//
	// };
	// }

	private void handleInternalMessage(InternalMessage msg) {
		switch (msg.messageID) {
		case InternalMessage.MESSAGE_LOGIN_STATE_CHANGE:
			// User user = (User) obj;
			User user = WePromoteApplication.getUser();
			Log.v(Constants.TAG, "login state changed to: " + user.isLoggedIn());
			if (user.isLoggedIn()) {
				Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
				if (user.getPromoterID(false) != null) {
					setupDone();
					return;
				}
				else {
					Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
					mWizardPagerAdapter.refreshFragment(1);
					mPager.setCurrentItem(1);
				}

			} else {
				mWizardPagerAdapter.refreshFragments();
			}
			validatePager(mPager.getCurrentItem());
			break;

		case InternalMessage.MESSAGE_FACEBOOK_EMAIL_RESOLVED:
			Log.v(Constants.TAG,"message: facebook email resolved");
			if (mWizardPagerAdapter == null)
				break;
			mWizardPagerAdapter.refreshFragment(1);
//			mPager.setCurrentItem(1);
			break;
		case InternalMessage.MESSAGE_PROMOTER_ID_SAVED:
			if (mPager == null)
				break;
			validatePager(mPager.getCurrentItem());
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
			mWizardPagerAdapter.refreshFragment(2);
			mPager.setCurrentItem(2);
			
			break;
		case InternalMessage.MESSAGE_LOGIN_FAILURE:
			Log.v(Constants.TAG, "login failed with error: " + msg.messageText);
			Utils.showToast(SetupWizardActivity.this,
					String.format("Login failure:\n%s", msg.messageText));
			break;
		case InternalMessage.MESSAGE_FACEBOOK_LOGIN_FAILURE:
			Log.v(Constants.TAG, "Facebook login failed with error: "
					+ msg.messageText);
			Utils.showToast(SetupWizardActivity.this, String.format(
					"Facebook Login failure:\n%s", msg.messageText));
			break;
		case InternalMessage.MESSAGE_SIGNUP_FAILURE:
			Log.v(Constants.TAG, "Signup failed with error: " + msg.messageText);
			Utils.showToast(SetupWizardActivity.this,
					String.format("Signup failure:\n%s", msg.messageText));
			break;
		case InternalMessage.MESSAGE_SETUP_DONE:
			setupDone();
			break;
			
		case InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER:
			boolean showSpinner = Boolean.valueOf(msg.messageText);
			setSpinnerDialog(getWindow().getDecorView().getRootView(),showSpinner);
			Utils.HideKeyboard(this, mPager);
			break;
		
		}
	}

	private void setupDone() {
		mWizardPagerAdapter = null;
		mPager = null;
		Utils.sendMessage(
				WePromoteApplication.getContext(),
				new InternalMessage(InternalMessage.MESSAGE_SELECT_MENU_ITEM,
						String.valueOf(MainActivity.MENU_ITEM_HOME)));
		finish();
	}

	SetupFragmentBase step1, step2, step3;

	private void initSetupPager() {

		mPager = (PagingControllableViewPager) findViewById(R.id.viewPager);
		mPager.removeAllViews();
		mWizardPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mWizardPagerAdapter);
		mPager.setOnPageChangeListener(onPagerChangeListener);

		step1 = SetupWizardStep1Fragment.getInstance("1", true);
		step2 = SetupWizardStep2Fragment.getInstance("2", true);
		step3 = SetupWizardStep3Fragment.getInstance("3", true);

		validatePager(mPager.getCurrentItem());

		mPager.setOnTouchListener(new OnSwipeTouchListener(this) {
			@Override
			public void onSwipeLeft() {
				isValidSwipeNext();
				super.onSwipeLeft();
			}

			@Override
			public void onSwipeRight() {
				super.onSwipeRight();
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		// GoogleAnalyticsAdapter.stopActivity(this);
	};

	OnPageChangeListener onPagerChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int step) {
			LayoutParams headerStepsLayout, headerStepsComplementLayout;

			int stepsWeight = 0, totalWeight = 6;
			validatePager(step);
			switch (step) {
			case 0:
				uncheckAllIndicators();
				mStep1Cubic
						.setBackgroundResource(R.drawable.page_indicator_cube_selected);
				stepsWeight = 3;
				getActionBar().setTitle(getResources().getString(R.string.txt_wizard_step1));
//				mTextView.setText();

				break;
			case 1:
				uncheckAllIndicators();
				mStep2Cubic
						.setBackgroundResource(R.drawable.page_indicator_cube_selected);
				stepsWeight = 4;
				getActionBar().setTitle(getResources().getString(R.string.txt_wizard_step2));
//				mTextView.setText();
				break;
			case 2:
				uncheckAllIndicators();
				mStep3Cubic
						.setBackgroundResource(R.drawable.page_indicator_cube_selected);
				stepsWeight = 5;
				getActionBar().setTitle(getResources().getString(R.string.txt_wizard_step3));
//				mTextView.setText();
				break;
			default:
				break;
			}

			headerStepsLayout = new LayoutParams(0, LayoutParams.MATCH_PARENT,
					stepsWeight);
//			mHeaderStep.setLayoutParams(headerStepsLayout);
//			headerStepsComplementLayout = new LayoutParams(0,LayoutParams.MATCH_PARENT, totalWeight - stepsWeight);
//			mHeaderStepsComplement.setLayoutParams(headerStepsComplementLayout);

		}

		private void uncheckAllIndicators() {
			mStep1Cubic.setBackgroundResource(R.drawable.page_indicator_cube);
			mStep2Cubic.setBackgroundResource(R.drawable.page_indicator_cube);
			mStep3Cubic.setBackgroundResource(R.drawable.page_indicator_cube);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	private void validatePager(int step) {
		switch (step) {
		case 0:
			if (WePromoteApplication.getUser() != null) {
				mPager.setPagingEnabled(WePromoteApplication.getUser()
						.isLoggedIn());
			}
			break;
		case 1:
			if (WePromoteApplication.getUser() != null
					&& WePromoteApplication.getUser().getPromoterID(false) != null) {
				mPager.setPagingEnabled(true);
			} else
				mPager.setPagingEnabled(false);
			break;
		case 2:

			break;
		default:
			break;
		}

	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public SetupFragmentBase getItem(int pos) {
			switch (pos) {

			case 0:
				return step1;// SetupWizardStep1Fragment.getInstance("FirstFragment, Instance 1",false);
			case 1:
				return step2;// SetupWizardStep2Fragment.getInstance("SecondFragment, Instance 1",false);
			case 2:
				return step3;// SetupWizardStep3Fragment.getInstance("ThirdFragment, Instance 1",false);

			}
			return null;

		}

		@Override
		public int getCount() {
			return 3;
		}

		public void refreshFragments() {
			for (int i = 0; i < getCount(); i++) {
				getItem(i).refresh();
			}
		}

		public void refreshFragment(int pagerID) {
			getItem(pagerID).refresh();
		}
	}

	public void setupCompleted() {
		// app.getPreferences().setSetupWizardActive(false);
		finish();
	}

	@Override
	public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.btn_prev:
		// mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		// break;
		// case R.id.btn_next:
		// if (mPager.getCurrentItem() == mWizardPagerAdapter.getCount() - 1)
		// {
		// setupCompleted();
		// return;
		// }
		// if (isValidSwipeNext())
		// mPager.setCurrentItem(mPager.getCurrentItem() + 1);
		//
		// break;
		// case R.id.btnCancel:
		//
		// cancelWizard();
		// break;
		// }

	}

	private boolean isValidSwipeNext() {
		if (mPager.getCurrentItem() == 0
				&& !WePromoteApplication.getUser().isLoggedIn()) {
			Utils.showToast(this, R.string.toast_first_login);
			Utils.sendMessage(WePromoteApplication.getContext(),new InternalMessage(InternalMessage.MESSAGE_SHOW_SETUP_WIZARD_SPINNER,"false"));
			return false;
		}
		return true;
	}

	private void cancelWizard() {

		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				SetupWizardActivity.this);// .create();

		// Setting Dialog Title
		alertDialog.setTitle("title");

		// Setting Dialog Message
		alertDialog.setMessage("message");

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.tick);

		alertDialog.setPositiveButton("ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						setupCompleted();
					}
				});

		alertDialog.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();

	}

	public static final int REQUEST_FACEBOOK_AUTHENTICATION = 32665;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (REQUEST_FACEBOOK_AUTHENTICATION):
			ParseFacebookUtils.finishAuthentication(requestCode, resultCode,
					data);

			break;
		}
	}

	// @Override
	// public void onParseLoginFailure(String message, boolean isFacebookLogin)
	// {
	// Log.v(Constants.TAG, "login failed with error: " + message);
	//
	// if (isFacebookLogin) {
	// Utils.showToast(this,
	// String.format("Facebook Login failure:\n%s",message));
	// } else {
	// Utils.showToast(this, String.format("Login failure:\n%s",message));
	// }
	// }

	// @Override
	// public void onParseLoginStateChange(User user) {
	//
	// }

	public class OnSwipeTouchListener implements OnTouchListener {

		private final GestureDetector gestureDetector;

		public OnSwipeTouchListener(Context context) {
			gestureDetector = new GestureDetector(context,
					new GestureListener());
		}

		public void onSwipeLeft() {
		}

		public void onSwipeRight() {
		}

		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}

		private final class GestureListener extends SimpleOnGestureListener {

			private static final int SWIPE_DISTANCE_THRESHOLD = 100;
			private static final int SWIPE_VELOCITY_THRESHOLD = 100;

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (e1 == null || e2 == null) {
					Log.e(Constants.TAG, "Fling Motion event are null");
					return false;
				}
				float distanceX = e2.getX() - e1.getX();
				float distanceY = e2.getY() - e1.getY();
				if (Math.abs(distanceX) > Math.abs(distanceY)
						&& Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
						&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
					if (distanceX > 0)
						onSwipeRight();
					else
						onSwipeLeft();
				}
				return false;
			}
		}
	}
	
//	private ProgressDialog ringProgressDialog;

	public void setSpinnerDialog(View view, boolean show) {
//		if (show) {
//			
//			ringProgressDialog = new ProgressDialog(this);
//			ringProgressDialog.setCancelable(false);
//			ringProgressDialog.setTitle("Please Wait...");
////			ringProgressDialog.setMessage("Please Wait ...");
////			ringProgressDialog.setProgressStyle(R.style.CustomProgressBar);
////			ringProgressDialog.setProgressStyle(R.style.CustomProgressBar);
////			ringProgressDialog.setIndeterminate(true);
////			ringProgressDialog.setProgressDrawable(	getResources().getDrawable(R.drawable.spinner));
//			ringProgressDialog.show();
//			
//
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//
//				}
//			}).start();
//		} else
//			ringProgressDialog.dismiss();
		
		if (show)
			mProgressLayout.setVisibility(View.VISIBLE);
		else
			mProgressLayout.setVisibility(View.GONE);
	}
}