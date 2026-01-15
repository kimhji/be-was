package model;

import common.Config;
import customException.UserExceptionConverter;

public class User {
    private String userId;
    private String password;
    private String name;
    private byte[] image;
    private String imagePath;

    public User(String userId, String password, String name) {
        if (userId == null || password == null || name == null
                || userId.isBlank() || password.isBlank() || name.isBlank())
            throw UserExceptionConverter.needUserData();
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.image = null;
        this.imagePath = Config.IMAGE_DEFAULT_PROFILE_NAME;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + "]";
    }
}
