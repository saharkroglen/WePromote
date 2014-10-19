package com.wepromote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.wepromote.common.CameraImp1;
import com.wepromote.common.CameraImp1.CameraListener;
import com.wepromote.common.ScalingUtilities;
import com.wepromote.common.Utils;
import com.wepromote.lib.Constants;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

public class CameraPreviewActivity extends Activity /*
															 */{
	
	private CameraImp1 mPreview;
	private static Camera mCamera;
	private ImageView mTakePicture;
	private ImageView mConfirmPhoto;
	private ImageView mDiscardPhoto;
	private ViewSwitcher mViewSwitcher_photo_frame;
	private ImageView mPreviewedImageView;
	private ImageView mSwitchCamera;
	private ImageView mFocusMeter;
	private boolean mAutoFocus = true;
	private RelativeLayout mVideoPreviewLayout;
	private static String imageID;
	private boolean mFlashBoolean = false;
	private SensorManager mSensorManager;
	private Sensor mAccel;
	private boolean mInitialized = false;
	private float mLastX = 0;
	private float mLastY = 0;
	private float mLastZ = 0;
	private Rect rec = new Rect();
	private boolean mShowLoadFromLibButton = true;
	private boolean mAllowCropping = true;
	private boolean mIsProfileImage = false;
	private int mScreenHeight;
	private int mScreenWidth;
	private boolean mInvalidate = false;
	// private String mProfileName;
	private ImageView mPhotoFrame;
	private int mPhotoFrameWidth, mPhotoFrameHeight;
	private File mPhoto = null;
//	private CustomTitle mTitle;
	private String mPhotoPath;

//	private boolean safeCameraOpen(int id) {
//	    boolean qOpened = false;
//	  
//	    try {
//	        releaseCameraAndPreview();
//	        mCamera = Camera.open(id);
//	        qOpened = (mCamera != null);
//	        mPreview.setCamera(mCamera);
//	    } catch (Exception e) {
//	        Log.e(getString(R.string.app_name), "failed to open Camera");
//	        e.printStackTrace();
//	    }
//
//	    return qOpened;    
//	}
//
	private void releaseCameraAndPreview() {

//	    if (mCamera != null) {
//	    	mCamera.stopPreview();
//	        mCamera.release();
//	        mCamera = null;
//	    }
		if (mPreview != null)
		{
			mPreview.releaseCamera();	
		}
		mCamera = null;		
	    if (mPreview != null)
	    {
	    	mPreview = null;
	    }
	}
	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance(){
		releaseCameraAndPreview();
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    	Log.e(Constants.TAG,e.getMessage());
	    }
	    return c; // returns null if camera is unavailable
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);		
		
		
//		if (savedInstanceState != null && savedInstanceState.getInt(REQUEST_CODE) != 0)
//		{
//			int requestCode = getIntent().getExtras().getInt(REQUEST_CODE);
//			getIntent().putExtra(REQUEST_CODE, requestCode);
//		}
	    
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mPhotoPath = extras.getString(Constants.EXTRA_PHOTO_PATH);
		}

		
		mVideoPreviewLayout = (RelativeLayout) findViewById(R.id.cameralayout);
		
		mSwitchCamera = (ImageView) findViewById(R.id.btn_switch_camera);
		if (Camera.getNumberOfCameras() == 1)
			mSwitchCamera.setVisibility(View.INVISIBLE);

		mSwitchCamera.setOnClickListener(onSwitchCamera);

		mPreviewedImageView = (ImageView) findViewById(R.id.previewedImage);
		mFocusMeter = (ImageView) findViewById(R.id.focus_meter);
		mViewSwitcher_photo_frame = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		mViewSwitcher_photo_frame.setDisplayedChild(0);

		android.view.ViewTreeObserver viewTreeObserver = mPreviewedImageView
				.getViewTreeObserver();
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
		}

		// the accelerometer is used for autofocus
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// get the window width and height to display buttons
		// according to device screen size
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		mScreenHeight = displaymetrics.heightPixels;
		mScreenWidth = displaymetrics.widthPixels;
		
		initCamera();
		
		FrameLayout previewLayout = (FrameLayout)findViewById(R.id.preview);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        previewLayout.addView(mPreview,params);
        
		// I need to get the dimensions of this drawable to set margins
		// for the ImageView that is used to take pictures
		Drawable mButtonDrawable = this.getResources().getDrawable(
				R.drawable.btn_shot);

		mTakePicture = (ImageView) findViewById(R.id.btn_shot);
		mTakePicture.setOnClickListener(takeShotListener);

		mConfirmPhoto = (ImageView) findViewById(R.id.btn_confirmPhoto);
		mConfirmPhoto.setOnClickListener(confirmDiscardPhotoListener);

		mDiscardPhoto = (ImageView) findViewById(R.id.btn_discardPhoto);
		mDiscardPhoto.setOnClickListener(confirmDiscardPhotoListener);

//		mSelectionSquare = (TouchView) findViewById(R.id.left_top_view);

	}
	
//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//	    // Save the user's current game state
//		int requestCode = getIntent().getExtras().getInt(REQUEST_CODE);		
//	    savedInstanceState.putInt(REQUEST_CODE, requestCode);
//	    
//	    
//	    // Always call the superclass so it can save the view hierarchy state
//	    super.onSaveInstanceState(savedInstanceState);
//	}
	private void initCamera() {		
		if (mCamera == null)
			mCamera = getCameraInstance();
		if (mPreview == null)
		{
			mPreview = new CameraImp1(this, mCamera,mScreenWidth,mScreenHeight,mFocusMeter);
			mPreview.setOnCameraListener(new CameraListener() {
				
				@Override
				public void onPictureTaken() {
					mPreview.stopPreview();
					mPreviewedImageView.setImageBitmap(ScalingUtilities.decodeResource(mPhotoPath, mScreenWidth, mScreenHeight, ScalingUtilities.ScalingLogic.FIT));
					mViewSwitcher_photo_frame.setDisplayedChild(1);
				}
			});
		}
	};

	OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
//			if (mPreview != null)
//				mPreview.startPreview();
		}
	};


	OnClickListener onSwitchCamera = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// onClick is invoked from main thread
			// kick-off a different Thread to handle this call
			Thread t = new Thread() {
				public void run() {
					// myViewer is the SurfaceView object which uses the camera
//					mCamera.SwitchCamera();
				}
			};
			t.start();

		}
	};

//	private void initCamera() {
////		showSpinner();
//		mCamera = new CameraImp1(CameraPreviewActivity.this, mPhotoFrameWidth,
//				mPhotoFrameHeight) {
//			Bitmap mBitmap = null;
//
//			public void PictureTaken(final Bitmap bitmap) {
////				savePhoto(bitmap, null, mIsProfileImage,PortalActivity.getCurrentProfile());
//				// mAutoFocus = true;
//				Log.v(Constants.TAG, " picture taken");
//				runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						mViewSwitcher_photo_frame.setDisplayedChild(1);
//						Log.v(Constants.TAG, "before load image");
//						// UtilsMedia.LoadImageFileToView(mPhoto.getPath(),
//						// mPreviewedImageView);
//						mPreviewedImageView.setImageBitmap(bitmap);
//						Log.v(Constants.TAG, "after load image");
////						mViewSwitcher_buttons_pane.setDisplayedChild(1);
////						if (mAllowCropping)
////							mSelectionSquare.setVisibility(View.VISIBLE);
//					}
//				});
//			}
//
//			public void FocusFailed() {
//
//			}
//
//			public void onError() {
//				finish();
//			}
//		};
//		mCamera.setOnCameraStateChange(new OnCameraStateChange() {
//			
//			@Override
//			public void onCameraPreviewStopped() {
////				hideLoadingProgressbar();				
//			}
//			
//			@Override
//			public void onCameraPreviewStarted() {
////				hideSpinner();				
//			}
//		});
//
//	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
	
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if (requestCode == RequestCode.REQUEST_CODE_PICK_PHOTO
//				&& resultCode == Activity.RESULT_OK) {
//			// System.gc();
//			String selectedImagePath = data
//					.getStringExtra(ExtraParams.EXTRA_SINGLE_PATH);
//
//			mViewSwitcher_photo_frame.setDisplayedChild(1);
//			if (mAllowCropping)
//				mSelectionSquare.setVisibility(View.VISIBLE);
//
//			imageID = UUID.randomUUID().toString();
//			String targetFilePath = String.format("%s%s%s.%s",
//					Constants.PROFILE_IMAGES_DIRECTORY, File.separator,
//					imageID, Constants.IMAGE_FORMAT_EXTENSION);
//			mPhoto = new File(targetFilePath);
//
//			Bitmap unscaledBitmap = ScalingUtilities.decodeResource(
//					selectedImagePath, mPhotoFrameWidth, mPhotoFrameHeight,
//					ScalingUtilities.ScalingLogic.FIT);
//
//			Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(
//					unscaledBitmap, mPhotoFrameWidth, mPhotoFrameHeight,
//					ScalingUtilities.ScalingLogic.FIT);
//			unscaledBitmap.recycle();
//			savePhoto(scaledBitmap, targetFilePath, true, null);
//			scaledBitmap.recycle();
//
//			UtilsMedia.LoadImageFileToView(targetFilePath, mPreviewedImageView);
//
//			mViewSwitcher_buttons_pane.setDisplayedChild(1);
//
//		}
//	}

	private static void copyFile(File source, File dest) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	// this is the autofocus call back
	

	private OnClickListener confirmDiscardPhotoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case (R.id.btn_confirmPhoto):
				
//					Intent resultIntent = new Intent().putExtra(ExtraParams.EXTRA_SINGLE_PATH, mPhoto.getPath());
					setResult(Activity.RESULT_OK, null);
				
				finish();
				break;
			case (R.id.btn_discardPhoto):
//				mPhoto.delete();
//				mCamera.StartPreview();
				mPreview.startPreview();
				mViewSwitcher_photo_frame.setDisplayedChild(0);
//				mViewSwitcher_buttons_pane.setDisplayedChild(0);
//				mSelectionSquare.setVisibility(View.INVISIBLE);
				// mFocusMeter.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	// This method takes the preview image, grabs the rectangular
	// part of the image selected by the bounding box and saves it.
	// A thread is needed to save the picture so not to hold the UI thread.
	private OnClickListener takeShotListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// if (mAutoFocus){
			// mAutoFocus = false;
			//mPreview.setCameraFocus(myAutoFocusCallback);
//			Wait.oneSec();
			Thread tGetPic = new Thread(new Runnable() {
				public void run() {
					Log.v(Constants.TAG, "take picture");
					File imageFile = null;
					try {
						imageFile = Utils.createImageFile(CameraPreviewActivity.this); 
					} catch (IOException ex) {
						// Error occurred while creating the File
					}
//					String mPhotoPath = null;
//					// Continue only if the File was successfully created
//					if (photoFile != null) {
//						mPhotoPath = Uri.fromFile(photoFile).getPath();
//						// ScalingUtilities.saveImageToStorage(mPhotoUri,bitmap);
////						Utils.saveBitmapToFile(bitmap, mPhotoPath);
//					}
					mPreview.takePicture(mPhotoPath);
				}
			});
			tGetPic.start();
			// }
			boolean pressed = false;
			if (!mTakePicture.isPressed()) {
				pressed = true;
			}
		}
	};

//	private File createImageFile() throws IOException {
//		// Create an image file name
//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
//				.format(new Date());
//		String imageFileName = "PNG_" + timeStamp + "_";
//		File storageDir = Utils.getAppImageStoragePath(this);
//		File image = File.createTempFile(imageFileName, /* prefix */
//				".png", /* suffix */
//				storageDir /* directory */
//		);
//
//		// Save a file: path for use with ACTION_VIEW intents
////		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//		return image;
//	}
	
	// just to close the app and release resources.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			return true;

		default:
			return super.onKeyDown(keyCode, event);
		}

	}

//	private boolean savePhoto(Bitmap bm, String existingFilePath,
//			boolean isProfileImage, Profile profile) {
//		FileOutputStream image = null;
//		try {
//			if (existingFilePath == null || existingFilePath.length() == 0) {
//				imageID = UUID.randomUUID().toString();
//				String fileName = String.format("%s.%s", imageID,
//						Constants.IMAGE_FORMAT_EXTENSION);
//				if (isProfileImage || profile == null) {
//					mPhoto = new File(Constants.PROFILE_IMAGES_DIRECTORY,
//							fileName);
//				} else {
//					if (profile != null && profile.Name.length() > 0) {
//						String profileDirectoryPath = Utils
//								.GetProfileExternalDirectory(profile.Name,
//										Constants.PROFILE_PHOTO_DIRECTORY);
//						String itemPath = String.format("%s%s%s",
//								profileDirectoryPath, File.separator, fileName);
//						mPhoto = new File(itemPath);
//					}
//				}
//			} else {
//				mPhoto = new File(existingFilePath);
//			}
//
//			image = new FileOutputStream(mPhoto);
//			bm.compress(CompressFormat.PNG, 100, image);
//
//			if (bm != null) {
//
//				// Log.i(TAG, "savePhoto(): Bitmap WxH is " + w + "x" + h);
//			} else {
//				// Log.i(TAG, "savePhoto(): Bitmap is null..");
//				return false;
//			}
//			return true;
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return false;
//		}
//
//	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		boolean intercept = false;
		switch (action) {
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_DOWN:
			float x = ev.getX();
			float y = ev.getY();
			// here we intercept the button press and give it to this
			// activity so the button press can happen and we can take
			// a picture.
			if ((x >= rec.left) && (x <= rec.right) && (y >= rec.top)
					&& (y <= rec.bottom)) {
				intercept = true;
			}
			break;
		}
		return intercept;
	}

	// mainly used for autofocus to happen when the user takes a picture
	// I also use it to redraw the canvas using the invalidate() method
	// when I need to redraw things.
	/*
	 * public void onSensorChanged(SensorEvent event) {
	 * 
	 * if (mInvalidate == true){ //mView.invalidate(); mInvalidate = false; }
	 * float x = event.values[0]; float y = event.values[1]; float z =
	 * event.values[2]; if (!mInitialized){ mLastX = x; mLastY = y; mLastZ = z;
	 * mInitialized = true; } float deltaX = Math.abs(mLastX - x); float deltaY
	 * = Math.abs(mLastY - y); float deltaZ = Math.abs(mLastZ - z);
	 * 
	 * if (deltaX > .5 && mAutoFocus){ //AUTOFOCUS (while it is not
	 * autofocusing) mAutoFocus = false;
	 * mPreview.setCameraFocus(myAutoFocusCallback); } if (deltaY > .5 &&
	 * mAutoFocus){ //AUTOFOCUS (while it is not autofocusing) mAutoFocus =
	 * false; mPreview.setCameraFocus(myAutoFocusCallback); } if (deltaZ > .5 &&
	 * mAutoFocus){ //AUTOFOCUS (while it is not autofocusing) mAutoFocus =
	 * false; mPreview.setCameraFocus(myAutoFocusCallback); }
	 * 
	 * //if (!mAutoFocus) // mFocusMeter.setImageResource(R.drawable.no_focus);
	 * mLastX = x; mLastY = y; mLastZ = z;
	 * 
	 * }
	 */

	// // extra overrides to better understand app lifecycle and assist
	// debugging
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// //Log.i(TAG, "onDestroy()");
	// }

	@Override
	public void onPause() {
		super.onPause();
		releaseCameraAndPreview();
		// Log.i(TAG, "onPause()");
		// mSensorManager.unregisterListener(this);
		// Application.SaveProfilesIndex(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		initCamera();
		// mSensorManager.registerListener(this, mAccel,
		// SensorManager.SENSOR_DELAY_UI);
		// Log.i(TAG, "onResume()");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// Log.i(TAG, "onRestart()");
	}

	@Override
	public void onStop() {
		super.onStop();
		// Log.i(TAG, "onStop()");
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Log.i(TAG, "onStart()");
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	
}