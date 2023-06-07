package com.ghovo.c_find_.models;

import java.io.Serializable;

public class User implements Serializable {

    public String userName, image, email, token, id, isLiked,number,notification;

    public String getUserName() {
        return userName;
    }

    public String getNumber() {
        return number;
    }
}
