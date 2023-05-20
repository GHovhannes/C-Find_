package com.ghovo.c_find_.activities;

import static com.ghovo.c_find_.utilities.Constants.KEY_ACTIVITY_FOR_SEARCH;
import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_HISTORY;
import static com.ghovo.c_find_.utilities.Constants.KEY_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_IS_LIKED;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_ID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.ghovo.c_find_.R;
import com.ghovo.c_find_.databinding.ActivityAccountBinding;
import com.ghovo.c_find_.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends BaseActivity {
    private ActivityAccountBinding activityAccountBinding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        activityAccountBinding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(activityAccountBinding.getRoot());
        loadUserDetails();
        setListeners();

        activityAccountBinding.distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                activityAccountBinding.seekbarText.setVisibility(View.VISIBLE);
                activityAccountBinding.seekbarText.setText("Distance from you " + progress + " metres");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        activityAccountBinding.activeOrNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DocumentReference documentReferenceStatus = firebaseFirestore.collection(KEY_COLLECTION_HISTORY)
                        .document(preferenceManager.getString(KEY_USER_ID));
                if(isChecked){

                    documentReferenceStatus.update(KEY_ACTIVITY_FOR_SEARCH, true);
                }
                else {

                    documentReferenceStatus.update(KEY_ACTIVITY_FOR_SEARCH, false);
                }
            }
        });

    }
    private void setListeners() {

        activityAccountBinding.searchImage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), HistoryActivity.class)));

        activityAccountBinding.homeImage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

    }
    private void loadUserDetails() {

        activityAccountBinding.accountImage.setImageBitmap(getResizedBitmap(getReceiverUserImage(
                preferenceManager.getString(KEY_IMAGE)
        )));
        activityAccountBinding.profileImage.setImageBitmap(getResizedBitmap(getReceiverUserImage(
                preferenceManager.getString(KEY_IMAGE)
        )));

        activityAccountBinding.homeImage.setImageResource(R.drawable.ic_home_filled);
        activityAccountBinding.searchImage.setImageResource(R.drawable.ic_search);

    }
    private Bitmap getReceiverUserImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }


    private Bitmap getResizedBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) 400) / width;
        float scaleHeight = ((float) 400) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();

        return resizedBitmap;

    }
}