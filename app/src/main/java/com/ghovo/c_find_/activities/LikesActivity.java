package com.ghovo.c_find_.activities;

import static com.ghovo.c_find_.utilities.Constants.KEY_ACTIVITY_FOR_SEARCH;
import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_HISTORY;
import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_REQUEST;
import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_USERS;
import static com.ghovo.c_find_.utilities.Constants.KEY_EMAIL;
import static com.ghovo.c_find_.utilities.Constants.KEY_FCM_TOKEN;
import static com.ghovo.c_find_.utilities.Constants.KEY_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_IS_LIKED;
import static com.ghovo.c_find_.utilities.Constants.KEY_LATITUDE;
import static com.ghovo.c_find_.utilities.Constants.KEY_LONGITUDE;
import static com.ghovo.c_find_.utilities.Constants.KEY_NUMBER;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_EMAIL;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_USER_NAME;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_EMAIL;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_NUMBER;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_USER_NAME;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_NAME;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;

import com.ghovo.c_find_.R;
import com.ghovo.c_find_.adapters.LikesAdapter;
import com.ghovo.c_find_.adapters.UsersAdapter;
import com.ghovo.c_find_.databinding.ActivityLikesBinding;
import com.ghovo.c_find_.databinding.ActivityMainBinding;
import com.ghovo.c_find_.dialogs.UserInfoDialog;
import com.ghovo.c_find_.listeners.DialogListener;
import com.ghovo.c_find_.listeners.UserListener;
import com.ghovo.c_find_.models.User;
import com.ghovo.c_find_.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;;import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LikesActivity extends BaseActivity implements UserListener, DialogListener {

    private Boolean isLiked;
    private User receiverUser;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private PreferenceManager preferenceManager;
    private ActivityLikesBinding activityLikesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        preferenceManager = new PreferenceManager(getApplicationContext());
        activityLikesBinding = ActivityLikesBinding.inflate(getLayoutInflater());
        setContentView(activityLikesBinding.getRoot());
        loadUserDetails();
        setListeners();
        getUsers();
    }
    private void setListeners() {
        activityLikesBinding.backToMain.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        activityLikesBinding.homeImage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        activityLikesBinding.searchImage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), HistoryActivity.class)));

        activityLikesBinding.accountImage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AccountActivity.class)));
    }
    private void loadUserDetails() {

        activityLikesBinding.accountImage.setImageBitmap(getResizedBitmap(getReceiverUserImage(
                preferenceManager.getString(KEY_IMAGE)
        )));

        activityLikesBinding.homeImage.setImageResource(R.drawable.ic_home_filled);
        activityLikesBinding.searchImage.setImageResource(R.drawable.ic_search);

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
    private void getUsers() {
        loading(true);
        String currentUserId = preferenceManager.getString(KEY_USER_ID);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(KEY_COLLECTION_REQUEST).whereEqualTo(KEY_RECEIVER_ID,currentUserId)
                .get()
                .addOnCompleteListener(task -> {

                    loading(false);

                    if (task.isSuccessful() && task.getResult() != null) {

                        List<User> userList = new ArrayList<>();

                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {

                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {

                                continue;

                            }

                                User user = new User();
                                user.userName = queryDocumentSnapshot.getString(KEY_SENDER_USER_NAME);
                                user.image = queryDocumentSnapshot.getString(KEY_SENDER_IMAGE);
                                user.email = queryDocumentSnapshot.getString(KEY_SENDER_NUMBER);
                                user.id = queryDocumentSnapshot.getId();
                                userList.add(user);

                        }

                        if (userList.size() > 0) {

                            LikesAdapter likesAdapter = new LikesAdapter(userList,this);
                            activityLikesBinding.usersRecyclerView.setAdapter(likesAdapter);

                            activityLikesBinding.usersRecyclerView.setVisibility(View.VISIBLE);

                        } else {

                            showErrorMessage();

                        }

                    } else {

                        showErrorMessage();

                    }
                });

    }

    private void showErrorMessage() {

        activityLikesBinding.textErrorMessage.setText(String.format("%s", "No User available"));
        activityLikesBinding.textErrorMessage.setVisibility(View.VISIBLE);

    }

    private void loading(Boolean isLoading) {

        if (isLoading) {

            activityLikesBinding.progressBar.setVisibility(View.VISIBLE);

        } else {

            activityLikesBinding.progressBar.setVisibility(View.INVISIBLE);

        }
    }
    @Override
    public void onUserClicked(User user) {

    }
    private void showDialog(String encodedImage) {


    }
    @Override
    public void onButtonClicked(Boolean flag) {

    }
    private void checkForHistory(String senderId, String receiverId) {

        firebaseFirestore.collection(KEY_COLLECTION_HISTORY)
                .whereEqualTo(KEY_SENDER_ID, senderId)
                .whereEqualTo(KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(historyOnCompleteListener);

    }

    private void checkForRequest(String senderId, String receiverId) {

        firebaseFirestore.collection(KEY_COLLECTION_REQUEST)
                .whereEqualTo(KEY_SENDER_ID, senderId)
                .whereEqualTo(KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(requestOnCompleteListener);

    }
    private final OnCompleteListener<QuerySnapshot> historyOnCompleteListener = task -> {

        if (!task.isSuccessful() || task.getResult() == null || task.getResult().getDocuments().size() == 0) {

            HashMap<String, Object> usersData = new HashMap<>();

            usersData.put(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID));
            usersData.put(KEY_RECEIVER_ID, receiverUser.id);
            usersData.put(KEY_RECEIVER_IMAGE, receiverUser.image);
            usersData.put(KEY_RECEIVER_USER_NAME, receiverUser.userName);
            usersData.put(KEY_RECEIVER_EMAIL, receiverUser.email);
            usersData.put(KEY_IS_LIKED, isLiked);

            firebaseFirestore.collection(KEY_COLLECTION_HISTORY)
                    .add(usersData);

        } else {

            DocumentReference documentReferenceStatus = firebaseFirestore.collection(KEY_COLLECTION_HISTORY)
                    .document(task.getResult().getDocuments().get(0).getId());

            documentReferenceStatus.update(KEY_IS_LIKED, isLiked);

        }

    };

    private final OnCompleteListener<QuerySnapshot> requestOnCompleteListener = task -> {

        if (!task.isSuccessful() || task.getResult() == null || task.getResult().getDocuments().size() == 0) {

            HashMap<String, Object> usersData = new HashMap<>();

            usersData.put(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID));
            usersData.put(KEY_SENDER_IMAGE, preferenceManager.getString(KEY_IMAGE));
            usersData.put(KEY_SENDER_USER_NAME, preferenceManager.getString(KEY_USER_NAME));
            usersData.put(KEY_SENDER_EMAIL, preferenceManager.getString(KEY_EMAIL));
            usersData.put(KEY_SENDER_NUMBER,preferenceManager.getString(KEY_NUMBER));
            usersData.put(KEY_RECEIVER_ID, receiverUser.id);

            firebaseFirestore.collection(KEY_COLLECTION_REQUEST)
                    .add(usersData);

        }

    };
}