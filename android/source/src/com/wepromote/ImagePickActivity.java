package com.wepromote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.wepromote.common.Constants;
import com.wepromote.common.ScalingUtilities;
import com.wepromote.common.Utils;
import com.wepromote.common.ScalingUtilities.ScalingLogic;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;

public class ImagePickActivity extends Activity {
	private static final String PREFS_PHOTO_PATH = "photoPathPrefs";
	private static final int UPLOAD_IMAGE_HEIGHT = 1000;
	private static final int UPLOAD_IMAGE_WIDTH = 1000;
	private static final int REQUEST_IMAGE_FROM_LIB = 1;
	static final int REQUEST_IMAGE_CAPTURE = 2;
	private Bitmap bitmap;
	private ImageView imageView;
//	private String mCurrentPhotoPath;
	private String mCampaignLink;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		imageView = (ImageView) findViewById(R.id.result);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mCampaignLink = extras.getString("campaignLink");
		}

		getImage();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v(Constants.TAG, "image picker destroy0");
	}

	public void getImage() {
		AlertDialog.Builder builder;

		// int sdk = android.os.Build.VERSION.SDK_INT;
		// if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
		// builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
		// android.R.style.Theme_Dialog));
		// } else {
		// builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
		// android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth));
		// }

		builder = new AlertDialog.Builder(this);
		final CharSequence[] choiceList = { "Take Photo", "Choose from library" };

		builder.setSingleChoiceItems(choiceList, -1, // does not select anything
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int index) {
						switch (index) {
						case 0:
							takePhoto();
							break;
						case 1:
							chooseFromLibrary();
							break;
						default:
							break;
						}
						dialog.dismiss();
					}
				});
		builder.setTitle("Select");
		builder.setCancelable(true);
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void chooseFromLibrary() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, REQUEST_IMAGE_FROM_LIB);
	}

	// private Uri mPhotoUri;
	private String mPhotoPath;

	private void takePhoto() {
	
//		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		// Ensure that there's a camera activity to handle the intent
//		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//			// Create the File where the photo should go
//			File photoFile = null;
//			try {
//				photoFile = createImageFile();
//
//			} catch (IOException ex) {
//				// Error occurred while creating the File
//			}
//			// Continue only if the File was successfully created
//			if (photoFile != null) {
//				Uri uri = Uri.fromFile(photoFile);
//				mPhotoPath = uri.getPath();
//
//				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//			}
//		}
		
		// Create the File where the photo should go
		File photoFile = null;
		try {
			photoFile = Utils.createImageFile(this);

		} catch (IOException ex) {
			// Error occurred while creating the File
		}
		// Continue only if the File was successfully created
		if (photoFile != null) {
			Uri uri = Uri.fromFile(photoFile);
			mPhotoPath = uri.getPath();

			Intent takePictureIntent = new Intent(this,CameraPreviewActivity.class);
			takePictureIntent.putExtra(Constants.EXTRA_PHOTO_PATH, mPhotoPath);
			startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
		}		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v(Constants.TAG, "image picker on pause");
		savePhotoPathToPrefs();
	}

	private void savePhotoPathToPrefs() {
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(PREFS_PHOTO_PATH, mPhotoPath);
		editor.commit();
		Log.v(Constants.TAG, "saving photo path to prefs" + mPhotoPath);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v(Constants.TAG, "image picker on resume");
		loadPhotoPathFromPrefs();
	}

	private void loadPhotoPathFromPrefs() {
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		mPhotoPath = sharedPref.getString(PREFS_PHOTO_PATH, null);
		Log.v(Constants.TAG, "loading photo path from prefs" + mPhotoPath);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		loadPhotoPathFromPrefs();
		InputStream stream = null;
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			
				ScalingUtilities.createScaledBitmapUsingDownsampling(mPhotoPath, UPLOAD_IMAGE_WIDTH,UPLOAD_IMAGE_HEIGHT, ScalingLogic.FIT);
			
		} else if (requestCode == REQUEST_IMAGE_FROM_LIB && resultCode == Activity.RESULT_OK)
		{
			try {
				if (data == null || data.getData() == null) {
					Utils.showToast(this, "No image was selected");
					finish();
					return;
				}
				// recyle unused bitmaps
				if (bitmap != null) {
					bitmap.recycle();
				}
				stream = getContentResolver().openInputStream(data.getData());
				// bitmap = ScalingUtilities.createScaledBitmap(stream,
				// imageView.getWidth(), imageView.getHeight(),
				// ScalingLogic.FIT); //BitmapFactory.decodeStream(stream);
				bitmap = ScalingUtilities.createScaledBitmap(stream,
						UPLOAD_IMAGE_WIDTH, UPLOAD_IMAGE_HEIGHT,
						ScalingLogic.FIT); // BitmapFactory.decodeStream(stream);

				File imageFile = null;
				try {
					imageFile = Utils.createImageFile(this);
//					mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
				} catch (IOException ex) {
					// Error occurred while creating the File
					Log.e(Constants.TAG,ex.getMessage());
				}
				// Continue only if the File was successfully created
				if (imageFile != null) {
					mPhotoPath = Uri.fromFile(imageFile).getPath();
					// ScalingUtilities.saveImageToStorage(mPhotoUri,bitmap);
					Utils.saveBitmapToFile(bitmap, mPhotoPath);
				}

				// imageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				{
					if (stream != null)
						try {
							stream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
		}

		Intent resultIntent = new Intent();
		resultIntent.putExtra("imagePath", mPhotoPath);
		resultIntent.putExtra("campaignLink", mCampaignLink);
		// TODO Add extras or a data URI to this intent as appropriate.
		if (resultCode == RESULT_OK)
			setResult(Activity.RESULT_OK, resultIntent);		
		finish();
	}

	
}