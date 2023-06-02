package com.ghovo.c_find_.utilities;

import java.util.HashMap;

public class Constants {

    public static final int IMAGE_WIDTH = 1920;
    public static final int IMAGE_HEIGHT = 1080;
    public static final String KEY_USER = "user";
    public static final String KEY_COUNT = "count";
    public static final String KEY_SEARCH = "count";

    public static final String KEY_SENDER_NUMBER = "senderNumber";
    public static final String KEY_NUMBER = "phone number";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_VERIFIED = "verify";
    public static final String KEY_IS_ONLINE = "online";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String KEY_IS_LIKED = "isLiked";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_SENDER_EMAIL = "senderEmail";
    public static final String KEY_COLLECTION_HISTORY = "history";
    public static final String KEY_COLLECTION_REQUEST = "request";
    public static HashMap<String, String> remoteMsgHeaders = null;
    public static final String KEY_RECEIVER_EMAIL = "receiverEmail";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_NETWORK_ACCESS = "network_access";
    public static final String URL = "https://fcm.googleapis.com/fcm/";
    public static final String KEY_SENDER_USER_NAME = "senderUserName";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String KEY_RECEIVER_USER_NAME = "receiverUserName";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> getRemoteMsgHeaders() {

        if (remoteMsgHeaders == null) {

            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(

                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA2OgU5DE:APA91bFGgrlpd2r2MSN3Pe08reUEYvxki6zWz1EU3_1vcVCwRweYKiK4O9ER6mea43oQrgGjYuMKZ2N-nif1aRBM2SJAaQrjbS38W0eVtpQHSgDtUNrcPY6oovjPr7296mV5Moxku0Vh"

            );

            remoteMsgHeaders.put(

                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"

            );

        }

        return remoteMsgHeaders;

    }

    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ACTIVITY_FOR_SEARCH = "is active";
}
