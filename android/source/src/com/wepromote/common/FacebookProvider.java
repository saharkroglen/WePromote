/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wepromote.common;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.wepromote.R;
import com.wepromote.WePromoteApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.*;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class FacebookProvider  {

    private static final String PERMISSION = "publish_actions";
    private static final Location SEATTLE_LOCATION = new Location("") {
        {
            setLatitude(47.6097);
            setLongitude(-122.3331);
        }
    };

    private final String PENDING_ACTION_BUNDLE_KEY = "com.wepromote:PendingAction";
    private FragmentActivity mContext;
    private Button postStatusUpdateButton;
    private Button postPhotoButton;
    private Button pickFriendsButton;
    private Button pickPlaceButton;
    private LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private ViewGroup controlsContainer;
    private GraphUser user;
    private GraphPlace place;
    private List<GraphUser> tags;
    private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }
    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

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
	

    public FacebookProvider(FragmentActivity c) {
    	mContext = c;
    	uiHelper = new UiLifecycleHelper(c, callback);
    }

    public void onCreate(Bundle savedInstanceState) {
        
//        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

//        setContentView(R.layout.main);

//        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
//            @Override
//            public void onUserInfoFetched(GraphUser user) {
//                HelloFacebookSampleActivity.this.user = user;
//                updateUI();
//                // It's possible that we were waiting for this.user to be populated in order to post a
//                // status update.
//                handlePendingAction();
//            }
//        });

//        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
//        greeting = (TextView) findViewById(R.id.greeting);
//
//        postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
//        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                onClickPostStatusUpdate();
//            }
//        });

//        postPhotoButton = (Button) findViewById(R.id.postPhotoButton);
//        postPhotoButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                onClickPostPhoto();
//            }
//        });
//
//        pickFriendsButton = (Button) findViewById(R.id.pickFriendsButton);
//        pickFriendsButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                onClickPickFriends();
//            }
//        });
//
//        pickPlaceButton = (Button) findViewById(R.id.pickPlaceButton);
//        pickPlaceButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                onClickPickPlace();
//            }
//        });
//
//        controlsContainer = (ViewGroup) findViewById(R.id.main_ui_container);

//        final FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
//        if (fragment != null) {
//            // If we're being re-created and have a fragment, we need to a) hide the main UI controls and
//            // b) hook up its listeners again.
//            controlsContainer.setVisibility(View.GONE);
//            if (fragment instanceof FriendPickerFragment) {
//                setFriendPickerListeners((FriendPickerFragment) fragment);
//            } else if (fragment instanceof PlacePickerFragment) {
//                setPlacePickerListeners((PlacePickerFragment) fragment);
//            }
//        }

//        // Listen for changes in the back stack so we know if a fragment got popped off because the user
//        // clicked the back button.
//        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                if (fm.getBackStackEntryCount() == 0) {
//                    // We need to re-show our UI.
//                    controlsContainer.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        // Can we present the share dialog for regular links?
        canPresentShareDialog = FacebookDialog.canPresentShareDialog(mContext,
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = FacebookDialog.canPresentShareDialog(mContext,
                FacebookDialog.ShareDialogFeature.PHOTOS);
    }

   
    public void onResume() {
   
        uiHelper.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(mContext);

//        updateUI();
    }

    
    public void onSaveInstanceState(Bundle outState) {
        uiHelper.onSaveInstanceState(outState);
        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	if (requestCode == Constants.REQUEST_CODE_CAPTURE_IMAGE_FOR_FACEBOOK && data != null && resultCode == Activity.RESULT_OK)
		{
    		Utils.showSpinner(mContext, true, "Uploading Photo...");
    		Bundle extras = data.getExtras();
			if (extras != null)
			{
				String path = extras.getString("imagePath"); 
				String campaignLink = extras.getString("campaignLink");
				postPhotoWithLink(mContext,campaignLink,path);
			}
			//mFacebookProvider.postPhotoWithLink(mContext.getActivity(),campaignLandingPageLink);
			uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
			
			return;
		}    	
//    	else
//    		Utils.showSpinner(WePromoteApplication.getContext(),false);
    }

    
    public void onPause() {    
        uiHelper.onPause();
    }

    
    public void onDestroy() {    
        uiHelper.onDestroy();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (pendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                exception instanceof FacebookAuthorizationException)) {
                new AlertDialog.Builder(mContext)
                    .setTitle(R.string.cancelled)
                    .setMessage(R.string.permission_not_granted)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            pendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
//        updateUI();
    }

//    private void updateUI() {
//        Session session = Session.getActiveSession();
//        boolean enableButtons = (session != null && session.isOpened());
//
//        postStatusUpdateButton.setEnabled(enableButtons || canPresentShareDialog);
//        postPhotoButton.setEnabled(enableButtons || canPresentShareDialogWithPhotos);
//        pickFriendsButton.setEnabled(enableButtons);
//        pickPlaceButton.setEnabled(enableButtons);
//
//        if (enableButtons && user != null) {
//            profilePictureView.setProfileId(user.getId());
//            greeting.setText(mContext.getString(R.string.hello_user, user.getFirstName()));
//        } else {
//            profilePictureView.setProfileId(null);
//            greeting.setText(null);
//        }
//    }

    @SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
                postPhoto();
//            	postPhotoWithLink();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }

    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = mContext.getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = mContext.getString(R.string.successfully_posted_post, message, id);
        } else {
            title = mContext.getString(R.string.error);
            alertMessage = error.getErrorMessage();
        }

        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
        return new FacebookDialog.ShareDialogBuilder(mContext)
                .setName("Hello Facebook")
                .setDescription("The 'Hello Facebook' sample application showcases simple Facebook integration")
                .setLink("http://developers.facebook.com/android");
    }

    private void postStatusUpdate() {
        if (canPresentShareDialog) {
            FacebookDialog shareDialog = createShareDialogBuilderForLink().build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (user != null && hasPublishPermission()) {
            final String message = mContext.getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
            Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), message, place, tags, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            showPublishResult(message, response.getGraphObject(), response.getError());
                        }
                    });
            request.executeAsync();
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    public void onClickPostPhoto() {
        performPublish(PendingAction.POST_PHOTO, canPresentShareDialogWithPhotos);
    }

    private FacebookDialog.PhotoShareDialogBuilder createShareDialogBuilderForPhoto(Bitmap... photos) {
        return new FacebookDialog.PhotoShareDialogBuilder(mContext)
                .addPhotos(Arrays.asList(photos));
    }

	public void postPhotoWithLink(Context c, String link,String imagePath) {
		uploadImage(c,link,imagePath);		
	}

	private void uploadImage(final Context c, final String link,String imagePath) {
//		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.vacation1);
		Bitmap bmp = BitmapFactory.decodeFile(imagePath);
		if (bmp == null)
		{
			Log.v(Constants.TAG,"No image taken. Upload to parse failed");
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
		byte[] data = stream.toByteArray();
//		byte[] data = "Working at Parse is great!".getBytes();
		final ParseFile file = new ParseFile(Utils.GetFileName(imagePath,true), data);

//		Utils.showSpinner(mContext, true, "Uploading Photo...");
		file.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				
				if (e != null)
					Log.e(Constants.TAG,"File upload failed: " + e.getMessage());
				else
				{
					// Handle success or failure here ...
					Log.v(Constants.TAG,"file uploaded into: " + file.getUrl());
					
					//associate uploaded file with parse object
					ParseObject sharedImage = new ParseObject("facebook_shared_image");
					sharedImage.put("promoterID", WePromoteApplication.getUser().getPromoterID(false));
					sharedImage.put("image_file", file);
					sharedImage.saveInBackground();
					
					//launch share dialog
					FacebookDialog.ShareDialogBuilder builder = new FacebookDialog.ShareDialogBuilder(
							mContext)
							.setLink(link)
//							.setApplicationName("Buzzinio")
							.setName(String.format("%s @%s", WePromoteApplication.getUser().getPromoterID(true),"hilton"))
							.setDescription("Had a great time!! try it out")
//							.setName("Great Deals Find You !")
							.setPicture(file.getUrl());
					// .setFragment(this);
					// share the app
					if (builder.canPresent()) {
						builder.build().present();
						Utils.showSpinner(mContext, false);
					}
				}
			}
		}, new ProgressCallback() {
			public void done(Integer percentDone) {
				// Update your progress spinner here. percentDone will be
				// between 0 and 100.
				Log.v(Constants.TAG,"upload progress: " + percentDone);
			}
		});
		
	}
	
    public void postPhoto() {
        Bitmap image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.instagram_post);
        if (canPresentShareDialogWithPhotos) {
            FacebookDialog shareDialog = createShareDialogBuilderForPhoto(image).build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (hasPublishPermission()) {
            Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), image, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    showPublishResult(mContext.getString(R.string.photo_post), response.getGraphObject(), response.getError());
                }
            });
            request.executeAsync();
        } else {
            pendingAction = PendingAction.POST_PHOTO;
        }
    }

    private void showPickerFragment(PickerFragment<?> fragment) {
        fragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
            @Override
            public void onError(PickerFragment<?> pickerFragment, FacebookException error) {
                String text = mContext.getString(R.string.exception, error.getMessage());
                Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        FragmentManager fm = mContext.getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();

        controlsContainer.setVisibility(View.GONE);

        // We want the fragment fully created so we can use it immediately.
        fm.executePendingTransactions();

        fragment.loadData(false);
    }

    private void onClickPickFriends() {
        final FriendPickerFragment fragment = new FriendPickerFragment();

        setFriendPickerListeners(fragment);

        showPickerFragment(fragment);
    }

    private void setFriendPickerListeners(final FriendPickerFragment fragment) {
        fragment.setOnDoneButtonClickedListener(new FriendPickerFragment.OnDoneButtonClickedListener() {
            @Override
            public void onDoneButtonClicked(PickerFragment<?> pickerFragment) {
                onFriendPickerDone(fragment);
            }
        });
    }

    private void onFriendPickerDone(FriendPickerFragment fragment) {
        FragmentManager fm = mContext.getSupportFragmentManager();
        fm.popBackStack();

        String results = "";

        List<GraphUser> selection = fragment.getSelection();
        tags = selection;
        if (selection != null && selection.size() > 0) {
            ArrayList<String> names = new ArrayList<String>();
            for (GraphUser user : selection) {
                names.add(user.getName());
            }
            results = TextUtils.join(", ", names);
        } else {
            results = mContext.getString(R.string.no_friends_selected);
        }

        showAlert(mContext.getString(R.string.you_picked), results);
    }

    private void onPlacePickerDone(PlacePickerFragment fragment) {
        FragmentManager fm = mContext.getSupportFragmentManager();
        fm.popBackStack();

        String result = "";

        GraphPlace selection = fragment.getSelection();
        if (selection != null) {
            result = selection.getName();
        } else {
            result = mContext.getString(R.string.no_place_selected);
        }

        place = selection;

        showAlert(mContext.getString(R.string.you_picked), result);
    }

    private void onClickPickPlace() {
        final PlacePickerFragment fragment = new PlacePickerFragment();
        fragment.setLocation(SEATTLE_LOCATION);
        fragment.setTitleText(mContext.getString(R.string.pick_seattle_place));

        setPlacePickerListeners(fragment);

        showPickerFragment(fragment);
    }

    private void setPlacePickerListeners(final PlacePickerFragment fragment) {
        fragment.setOnDoneButtonClickedListener(new PlacePickerFragment.OnDoneButtonClickedListener() {
            @Override
            public void onDoneButtonClicked(PickerFragment<?> pickerFragment) {
                onPlacePickerDone(fragment);
            }
        });
        fragment.setOnSelectionChangedListener(new PlacePickerFragment.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(PickerFragment<?> pickerFragment) {
                if (fragment.getSelection() != null) {
                    onPlacePickerDone(fragment);
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoSession) {
        Session session = Session.getActiveSession();
        if (session != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else if (session.isOpened()) {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(mContext, PERMISSION));
                return;
            }
        }

        if (allowNoSession) {
            pendingAction = action;
            handlePendingAction();
        }
    }
}
