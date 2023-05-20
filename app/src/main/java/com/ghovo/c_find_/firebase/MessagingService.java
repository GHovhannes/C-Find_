package com.ghovo.c_find_.firebase;

import static com.ghovo.c_find_.utilities.Constants.KEY_FCM_TOKEN;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_IMAGE;
import static com.ghovo.c_find_.utilities.Constants.KEY_RECEIVER_USER_NAME;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_ID;
import static com.ghovo.c_find_.utilities.Constants.KEY_USER_NAME;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ghovo.c_find_.R;
import com.ghovo.c_find_.activities.MainActivity;
import com.ghovo.c_find_.models.User;
import com.ghovo.c_find_.utilities.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {

        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        super.onMessageReceived(message);

        PreferenceManager preferenceManager = new PreferenceManager(this);

        User user = new User();

        user.id = message.getData().get(KEY_USER_ID);
        user.token = message.getData().get(KEY_FCM_TOKEN);
        user.userName = message.getData().get(KEY_USER_NAME);

        preferenceManager.putString(KEY_RECEIVER_ID, user.id);
        preferenceManager.putString(KEY_RECEIVER_USER_NAME, user.userName);

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(KEY_USER, user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        builder.setSmallIcon(R.drawable.ic_heart);
        builder.setContentTitle(user.userName);
        builder.setContentText("Liked");
        builder.setLargeIcon(getReceiverUserImage(message.getData().get(KEY_RECEIVER_IMAGE)));

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence channelName = "Chat'T";
            String channelDescription = "This notification channel is used for chat message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            return;

        }

        notificationManagerCompat.notify(notificationId, builder.build());

    }

    private Bitmap getReceiverUserImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }
}
