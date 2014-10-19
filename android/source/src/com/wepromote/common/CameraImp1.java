package com.wepromote.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import com.wepromote.R;
import com.wepromote.lib.Constants;
public class CameraImp1 extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
	private int mScreenWidth;
	private int mScreenHeight;
    private static Camera mCamera;
    private Handler mHandler;
    private boolean mFocused;
    private ImageView mFocusMeter;
    private Activity mContext;
    private String mPhotoPath;
	private int mImageRotation;
	
	public interface CameraListener {
		void onPictureTaken();
	}

	private CameraListener mListener;
	public void setOnCameraListener(CameraListener listener)
	{
		mListener = listener;
	}
	
    public CameraImp1(Activity context, Camera camera, int ScreenWidth, int ScreenHeight,ImageView focusMeter) {
        super(context);
        mContext = context;
        mCamera = camera;
        mHandler = new Handler();
        mScreenWidth = ScreenWidth;
        mScreenHeight = ScreenHeight;
        mFocusMeter = focusMeter;
        Camera.Parameters parameters = mCamera.getParameters();
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
        	setCameraDimensionsParams(parameters,ScreenWidth,ScreenHeight);
		} 
		setQualityParams(parameters);
		setExposureSceneParams(parameters);
		setFocusParams(parameters);
		setAntibandingParams(parameters);		
		mImageRotation = adjustCameraDisplayOrientation(context, 0, mCamera);
		try {
//			camera.stopPreview(); // before setting params, stop previewing (cause crash in old galaxy tab)
			camera.setParameters(parameters);
		} catch (Exception e) {
			Log.e(Utils.TAG,"Exception in surfaceCreated - camera.setParameters");
			e.printStackTrace();
			// suppress exception since some devices (like nexus5 and old
			// devices) don't support setParameters api - see following issue:
			// Issue 24563: Camera setParameters crashes on setting preview size
			// - https://code.google.com/p/android/issues/detail?id=24563
		}

		try {
			parameters = camera.getParameters();
		} catch (Exception e) {
			Log.e(Utils.TAG,"Exception in surfaceCreated - camera.getParameters");
		}

		int mCameraImageFormat = parameters.getPreviewFormat(); // ImageFormat.RGB_565;
		Log.d(Utils.TAG, "Current default video format: " + mCameraImageFormat);
		Size previewSize = parameters.getPreviewSize();
		Log.d(Utils.TAG, "Current default preview size: "
				+ ((Integer) (previewSize.width)).toString() + " x "
				+ ((Integer) (previewSize.height)).toString());
		Size pictureSize = parameters.getPictureSize();
		Log.d(Utils.TAG, "Current default picture size: "
				+ ((Integer) (pictureSize.width)).toString() + " x "
				+ ((Integer) (pictureSize.height)).toString());


        // Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					setCameraFocus(myAutoFocusCallback);
				}
				return false;
			}
		});
        
    }
    
    private boolean mTakePicPressed = false;
    public void takePicture(String path) {
    	mPhotoPath = path;
		Log.i(Utils.TAG, "takePicture");
		try {
			mTakePicPressed = true;
			setCameraFocus(myAutoFocusCallback);
			
		} catch (Exception e) {
//			onCameraFailure();
			Log.e(Utils.TAG, "Exception in take picture: " + e.getMessage());
		}
	}
    
	ShutterCallback myShutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.i(Utils.TAG, "onShutter");
		}
	};

	PictureCallback myPictureCallback_RAW = new PictureCallback() {
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			Log.i(Utils.TAG, "onPictureTaken");
		}
	};

	PictureCallback myPictureCallback_JPG = new PictureCallback() {
		

		public void onPictureTaken(byte[] arg0, Camera arg1) {
			String msg;
//			System.gc();
			Log.e(Utils.TAG, "myPictureCallback_JPG called");
			try {				
				stopPreview();
			} catch (Exception e) {
				msg = "Exception in stop preview";
//				BugSenseHandler.sendEvent(msg);
				Log.e(Utils.TAG, msg);
//				onCameraFailure();
				return;
			}

			// this is is from takepic
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			opt.inDither = false; // Disable Dithering mode
			opt.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
			opt.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
			opt.inTempStorage = new byte[32 * 1024];

//			opt.inSampleSize = ScalingUtilities.calculateSampleSize(mCamera
//					.getParameters().getPictureSize().width, mCamera
//					.getParameters().getPictureSize().height,
//					1000, 1000,
//					ScalingLogic.FIT);
			opt.inSampleSize = 1;

			Bitmap bitmapInput = null;

			try {
				bitmapInput = BitmapFactory.decodeByteArray(arg0, 0,
						arg0.length, opt);
				Bitmap rotatedBitmap = Utils.safeBitmapCopy(bitmapInput, true, mImageRotation);
				Utils.saveBitmapToFile(rotatedBitmap, mPhotoPath);
//				new Utils.SavePhotoByteArrayTask(mPath).execute(bitmapInput);
			} catch (Exception e) {
				msg = "Exception decoding picture byte array";
				Log.e(Utils.TAG, e.getMessage());
//				BugSenseHandler.sendEvent(msg);
//				onCameraFailure();
				return;
			}

			if (bitmapInput == null) {
				msg = "Failed to get bitmap from camera";
				Log.e(Utils.TAG, msg);
//				BugSenseHandler.sendEvent(msg);
//				onCameraFailure();
				return;

			}

			mListener.onPictureTaken();//(bitmapInput);

		}

		private Bitmap safeBitmapCopy(Bitmap sourceBitmap, boolean rotate,
				int angle) {
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);

			Bitmap copiedBitmap = null;

			int compressionPercent = 100;

			while ((copiedBitmap == null) && compressionPercent > 20) {
				Log.i(Utils.TAG, "Fixing compression rate:"
						+ compressionPercent);
				try {
					if (compressionPercent != 100) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						sourceBitmap.compress(CompressFormat.JPEG,
								compressionPercent, bos);

						Bitmap oldBitmap = sourceBitmap;

						sourceBitmap = BitmapFactory
								.decodeStream(new ByteArrayInputStream(bos
										.toByteArray()));

						oldBitmap.recycle();
						System.gc();
					}

					if (rotate) {
						copiedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
								sourceBitmap.getWidth(),
								sourceBitmap.getHeight(), matrix, false);
					} else {
						copiedBitmap = sourceBitmap.copy(Config.ARGB_8888,
								false);
					}
				} catch (OutOfMemoryError e) {
					Log.e(Utils.TAG,
							"Out of memory exception creating compressed picture");
					compressionPercent -= 5;
				}
			}

			return copiedBitmap;
		}
	};
    
    private void setQualityParams(Camera.Parameters parameters) {
		// set jpeg quality to max
		parameters.setJpegQuality(100);
	}

	private void setExposureSceneParams(Camera.Parameters parameters) {
		parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
		parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
	}

	private void setAntibandingParams(Camera.Parameters parameters) {
		parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
	}

	private void setFocusParams(Camera.Parameters parameters) {
		List<String> focusModeList = parameters.getSupportedFocusModes();
		boolean supportAutoFocus = false;
		for (String focusModeName : focusModeList) {
			if (focusModeName
					.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_AUTO))
				supportAutoFocus = true;
		}
		// parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		if (supportAutoFocus)
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		
	}
    private void setCameraDimensionsParams(Camera.Parameters parameters,int requestedWidth,int requestedHeight) {
    	
    	
    	Log.i(Utils.TAG, "surfaceCreated:SDK>=8");

		// if (context.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_PORTRAIT)
		// {
		Log.i(Utils.TAG, "surfaceCreated:Portrait orientation");
		List<Size> supportedPreviewSize = parameters.getSupportedPreviewSizes();

		boolean cont = true;
		Size size = null;
		// we prefer a smaller size (around 640x480) to lower system resources
		// while previewing.

		if (!searchCameraDimentionsRequestedSize(supportedPreviewSize, requestedWidth, requestedHeight)) {
			if (!searchCameraDimentionsInProximityToRequestedSize(
					supportedPreviewSize, requestedWidth, requestedHeight, 150)) {
				mSelectedPreviewSize = supportedPreviewSize.get(0);
			}
		}

		parameters.setPreviewSize(mSelectedPreviewSize.width,
				mSelectedPreviewSize.height);
		parameters.setPictureSize(mSelectedPreviewSize.width,
				mSelectedPreviewSize.height);

//		mCamera.setParameters(parameters);
	
//		setDisplayOrientation();
		
	}
    private boolean searchCameraDimentionsInProximityToRequestedSize(
			List<Size> supportedPreviewSize, int requestedWidth,
			int requestedHeight, int proximityFactor) {
		Iterator<Size> itr = supportedPreviewSize.iterator();
		Size size;
		while (itr.hasNext()) {
			size = itr.next();
			if (size.height <= requestedWidth) {
				float ratio = (float) size.height / (float) size.width;
				if (ratio == 0.75f) {
					// find the smallest preview size which supports ratio of
					// 0.75 and is closest to 640x480
					mSelectedPreviewSize = size;
					int heightGapRange = size.height - requestedHeight;
					int widthGapRange = size.width - requestedWidth;
					if ((heightGapRange >= 0 && heightGapRange < proximityFactor)
							&& (widthGapRange >= 0 && widthGapRange < proximityFactor)) {
						return true;
						// break;
					}
				}
			}
		}
		return false;
	}

    private Size mSelectedPreviewSize = null;
    private boolean searchCameraDimentionsRequestedSize(
			List<Size> supportedPreviewSize, int requestedWidth,
			int requestedHeight) {
		Iterator<Size> itr = supportedPreviewSize.iterator();
		Size size;
		while (itr.hasNext()) {
			size = itr.next();
			if (size.height == requestedHeight && size.width == requestedWidth ||
					size.height == requestedWidth && size.width == requestedHeight) {				
					mSelectedPreviewSize = size;
					return true;				
			}
		}
		return false;
	}

	public void releaseCamera()
    {
    	mCamera.stopPreview();
    	mCamera.release();    	
    	mCamera = null;
    }

    /*
     * to make the camera image show in the same orientation as the display call this method
     */
    public int adjustCameraDisplayOrientation(Activity activity,
            int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        return result;
    }
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	if (mCamera != null)
        	{
	            mCamera.setPreviewDisplay(holder);
	            mCamera.startPreview();
        	}
        } catch (IOException e) {
            Log.d(Constants.TAG, "Error setting camera preview: " + e.getMessage());
        }
    }
    
    public void startPreview()
    {
    	mCamera.startPreview();
    }
    public void stopPreview()
    {
    	mCamera.stopPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

    	if (mCamera == null)
    		return;
    	
        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(Constants.TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

	public void setCameraFocus(AutoFocusCallback autoFocusCallback) {
		mContext.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mFocusMeter.setVisibility(View.VISIBLE);
				mFocusMeter.setImageResource(R.drawable.no_focus);				
			}
		});
		
		mCamera.autoFocus(autoFocusCallback);		
	}
	private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
		public void onAutoFocus(final boolean autoFocusSuccess, Camera arg1) {
			// Wait.oneSec();
			mContext.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Log.v(Constants.TAG,"camera focus status is: " + autoFocusSuccess);
					mFocused = autoFocusSuccess;
					mFocusMeter.setVisibility(View.VISIBLE);
					if (mFocused)
					{
						mFocusMeter.setImageResource(R.drawable.focus);
						if(mTakePicPressed)
						{
							mCamera.takePicture(myShutterCallback, myPictureCallback_RAW,
									myPictureCallback_JPG);
							mTakePicPressed = false;
						}
					}
					else
						mFocusMeter.setImageResource(R.drawable.no_focus);
					
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							Log.v(Constants.TAG,"hide focus meter");
							mFocusMeter.setVisibility(View.INVISIBLE);
						}
					}, 1000);
				}
			});
			
		}
	};
}