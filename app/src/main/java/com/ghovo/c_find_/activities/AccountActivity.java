package com.ghovo.c_find_.activities;

import static android.content.ContentValues.TAG;
import static com.ghovo.c_find_.utilities.Constants.IMAGE_HEIGHT;
import static com.ghovo.c_find_.utilities.Constants.IMAGE_WIDTH;
import static com.ghovo.c_find_.utilities.Constants.KEY_ACTIVITY_FOR_SEARCH;
import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_HISTORY;
import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_USERS;
import static com.ghovo.c_find_.utilities.Constants.KEY_DISTANCE;
import static com.ghovo.c_find_.utilities.Constants.KEY_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_IS_LIKED;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_ID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.ghovo.c_find_.R;
import com.ghovo.c_find_.databinding.ActivityAccountBinding;
import com.ghovo.c_find_.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class AccountActivity extends BaseActivity {
    private ActivityAccountBinding activityAccountBinding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private int progress;
    private boolean flagIsChecked;
    private String encodedImage;


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    if (result.getData() != null) {

                        Uri imageUri = result.getData().getData();

                        try {

                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            activityAccountBinding.profileImage.setImageBitmap(bitmap);
                            encodedImage = encodedImage(bitmap);

                        } catch (FileNotFoundException e) {

                            e.printStackTrace();

                        }
                    }
                }
            }
    );

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
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                activityAccountBinding.seekbarText.setVisibility(View.VISIBLE);
                activityAccountBinding.seekbarText.setText("Distance from you " + i + " metres");
                activityAccountBinding.saveChanges.setVisibility(View.VISIBLE);
                progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        activityAccountBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
                activityAccountBinding.saveChanges.setVisibility(View.VISIBLE);
            }
        });

        activityAccountBinding.activeOrNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flagIsChecked = isChecked;
                activityAccountBinding.saveChanges.setVisibility(View.VISIBLE);
            }
        });

        activityAccountBinding.saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReferenceStatus = firebaseFirestore.collection(KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(KEY_USER_ID));
                documentReferenceStatus.update(KEY_ACTIVITY_FOR_SEARCH, flagIsChecked);
                documentReferenceStatus.update(KEY_DISTANCE, String.valueOf(progress));
                documentReferenceStatus.update(KEY_IMAGE,encodedImage);
                preferenceManager.putString(KEY_IMAGE,encodedImage);
                loadUserDetails();
                activityAccountBinding.saveChanges.setVisibility(View.GONE);
                Toast.makeText(AccountActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
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
    private String encodedImage(Bitmap bitmap) {

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.WEBP, 95, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }
}