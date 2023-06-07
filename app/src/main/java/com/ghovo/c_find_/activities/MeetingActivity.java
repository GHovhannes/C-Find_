package com.ghovo.c_find_.activities;

import static com.ghovo.c_find_.utilities.Constants.KEY_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_NUMBER;
import static com.ghovo.c_find_.utilities.Constants.KEY_PLACE;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_USER_NAME;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_NUMBER;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_USER_NAME;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ghovo.c_find_.R;
import com.ghovo.c_find_.databinding.ActivityMeetingBinding;
import com.ghovo.c_find_.utilities.PreferenceManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MeetingActivity extends BaseActivity {
    ActivityMeetingBinding activityMeetingBinding;
    String current,receiver,place,image,number;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        image = preferenceManager.getString(KEY_IMAGE);
        number = preferenceManager.getString(KEY_NUMBER);
        activityMeetingBinding = ActivityMeetingBinding.inflate(getLayoutInflater());
        setContentView(activityMeetingBinding.getRoot());
        Intent intent = getIntent();
        current = intent.getStringExtra(KEY_SENDER_USER_NAME);
        receiver = intent.getStringExtra(KEY_RECEIVER_USER_NAME);
        activityMeetingBinding.pickPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MeetingActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        activityMeetingBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (place!=null) {
                    FirebaseFirestore fb = FirebaseFirestore.getInstance();
                    number = preferenceManager.getString(KEY_NUMBER);
                    Log.d("Hello", "number: " + number);
                    HashMap<String, Object> meeting = new HashMap<>();
                    meeting.put(KEY_SENDER_USER_NAME, current);
                    meeting.put(KEY_RECEIVER_USER_NAME, receiver);
                    meeting.put(KEY_SENDER_IMAGE,image);
                    meeting.put(KEY_PLACE, place);
                    fb.collection("meetings").add(meeting);
                    Intent intent1 = new Intent(MeetingActivity.this, LikesActivity.class);
                    startActivity(intent1);
                }else {
                    Toast.makeText(MeetingActivity.this, "Please, choose a place you want to meet him/her", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        place = uri.toString();
        activityMeetingBinding.place.setImageURI(uri);
    }
}