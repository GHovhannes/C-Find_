package com.ghovo.c_find_.models;

import java.io.Serializable;

public class User implements Serializable {

    public String userName, image, email, token, id, isLiked;

    public String getUserName() {
        return userName;
    }
}
