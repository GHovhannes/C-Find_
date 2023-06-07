package com.ghovo.c_find_.activities;

import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_HISTORY;
import static com.ghovo.c_find_.utilities.Constants.KEY_COLLECTION_REQUEST;
import static com.ghovo.c_find_.utilities.Constants.KEY_DISTANCE;
import static com.ghovo.c_find_.utilities.Constants.KEY_EMAIL;
import static com.ghovo.c_find_.utilities.Constants.KEY_FCM_TOKEN;
import static com.ghovo.c_find_.utilities.Constants.KEY_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_IS_LIKED;
import static com.ghovo.c_find_.utilities.Constants.KEY_LATITUDE;
import static com.ghovo.c_find_.utilities.Constants.KEY_LONGITUDE;
import static com.ghovo.c_find_.utilities.Constants.KEY_NUMBER;
import static com.ghovo.c_find_.utilities.Constants.KEY_PLACE;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_EMAIL;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_USER_NAME;
import static com.ghovo.c_find_.utilities.Constants.KEY_SEARCH;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_EMAIL;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_NUMBER;
import static com.ghovo.c_find_.utilities.Constants.KEY_SENDER_USER_NAME;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.ghovo.c_find_.R;
import com.ghovo.c_find_.adapters.LikesAdapter;
import com.ghovo.c_find_.adapters.NotificationsAdapter;
import com.ghovo.c_find_.adapters.UsersAdapter;
import com.ghovo.c_find_.databinding.ActivityLikesBinding;
import com.ghovo.c_find_.databinding.ActivityNotificationsBinding;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationsActivity extends BaseActivity implements UserListener, DialogListener {
    private Boolean isLiked;
    private User receiverUser;
    String currentUserName;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private PreferenceManager preferenceManager;
    private ActivityNotificationsBinding activityNotificationsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        preferenceManager = new PreferenceManager(getApplicationContext());
        activityNotificationsBinding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(activityNotificationsBinding.getRoot());
        loadUserDetails();
        setListeners();
        getUsers();
        activityNotificationsBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activityNotificationsBinding.swipeRefresh.setRefreshing(false);
                getUsers();
            }
        });
    }
    private void setListeners() {
        activityNotificationsBinding.backToMain.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        activityNotificationsBinding.homeImage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));

        activityNotificationsBinding.searchImage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), HistoryActivity.class)));

        activityNotificationsBinding.accountImage.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AccountActivity.class)));

    }
    private void loadUserDetails() {

        activityNotificationsBinding.accountImage.setImageBitmap(getResizedBitmap(getReceiverUserImage(
                preferenceManager.getString(KEY_IMAGE)
        )));

        activityNotificationsBinding.homeImage.setImageResource(R.drawable.ic_home_filled);
        activityNotificationsBinding.searchImage.setImageResource(R.drawable.ic_search);

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
        //loading(true);
        currentUserName = preferenceManager.getString(KEY_USER_NAME);
        firebaseFirestore.collection("meetings").whereEqualTo(KEY_RECEIVER_USER_NAME,currentUserName).get().addOnCompleteListener(task -> {

            loading(false);
            String currentUserName = preferenceManager.getString(KEY_USER_NAME);

            if (task.isSuccessful() && task.getResult() != null) {

                List<User> userList = new ArrayList<>();

                for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                    if (!currentUserName.equals(queryDocumentSnapshot.getString(KEY_SENDER_USER_NAME))){
                        User user = new User();
                        user.userName = queryDocumentSnapshot.getString(KEY_SENDER_USER_NAME);
                        user.image = queryDocumentSnapshot.getString(KEY_SENDER_IMAGE);
                        user.notification = queryDocumentSnapshot.getString(KEY_PLACE);
                        user.id = queryDocumentSnapshot.getId();
                        userList.add(user);
                    }
                }

                if (userList.size() > 0) {
                    NotificationsAdapter nfa = new NotificationsAdapter(userList,this);
                    activityNotificationsBinding.usersRecyclerView.setAdapter(nfa);


                } else {

                    showErrorMessage();

                }

            } else {

                showErrorMessage();

            }
        });
    }

    private void showErrorMessage() {

        activityNotificationsBinding.textErrorMessage.setText(String.format("%s", "No User available"));
        activityNotificationsBinding.textErrorMessage.setVisibility(View.VISIBLE);

    }

    private void loading(Boolean isLoading) {

        if (isLoading) {

            activityNotificationsBinding.progressBar.setVisibility(View.VISIBLE);

        } else {

            activityNotificationsBinding.progressBar.setVisibility(View.INVISIBLE);

        }
    }
    @Override
    public void onUserClicked(User user) {
        showDialog(user.image);

    }
    private void showDialog(String encodedImage){
        FragmentManager fragmentManager = getSupportFragmentManager();
        UserInfoDialog userInfoDialog = new UserInfoDialog(getReceiverUserImage(encodedImage), this);
        userInfoDialog.show(fragmentManager, "dialog");
    }


    @Override
    public void onButtonClicked(Boolean flag) {

    }
}