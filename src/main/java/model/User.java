package model;

import common.Config;
import customException.UserExceptionConverter;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    private byte[] image;
    private String imagePath;

    public User(String userId, String password, String name, String email) {
        if (userId == null || password == null || name == null || email == null
                || userId.isBlank() || password.isBlank() || name.isBlank() || email.isBlank())
            throw UserExceptionConverter.needUserData();
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
