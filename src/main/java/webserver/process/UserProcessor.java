package webserver.process;

import common.Auth;
import common.Config;
import common.Utils;
import customException.UserExceptionConverter;
import db.Database;
import db.ImageManager;
import model.User;
import webserver.http.Request;
import webserver.http.RequestBody;

import java.util.Optional;

import static webserver.process.StaticFileProcessor.checkImageType;

public class UserProcessor {
    public byte[] createUser(Request request) {
        User user = new User(Optional.ofNullable(request.bodyParam.get("userId"))
                .map(RequestBody::getContentString)
                .orElseThrow(UserExceptionConverter::needUserData),
                Optional.ofNullable(request.bodyParam.get("password"))
                        .map(RequestBody::getContentString)
                        .orElseThrow(UserExceptionConverter::needUserData),
                Optional.ofNullable(request.bodyParam.get("name"))
                        .map(RequestBody::getContentString)
                        .orElseThrow(UserExceptionConverter::needUserData));

        if (Database.findUserById(user.getUserId()) != null) throw UserExceptionConverter.conflictUserID();
        if (Database.findUserByName(user.getName()) != null) throw UserExceptionConverter.conflictUserName();

        if (user.getUserId().length() < Config.MIN_USER_DATA_LENGTH) throw UserExceptionConverter.tooShortUserId();
        if (user.getName().length() < Config.MIN_USER_DATA_LENGTH) throw UserExceptionConverter.tooShortUserName();
        if (user.getPassword().length() < Config.MIN_USER_DATA_LENGTH)
            throw UserExceptionConverter.tooShortUserPassword();
        Database.addUser(user);

        return user.toString().getBytes();
    }

    public String loginUser(Request request) {
        String reqPassword = Optional.ofNullable(request.bodyParam.get("password"))
                .map(RequestBody::getContentString)
                .orElseThrow(UserExceptionConverter::needUserData);

        if (reqPassword == null || reqPassword.isBlank()) throw UserExceptionConverter.needUserData();
        User user = Database.findUserById(Optional.ofNullable(request.bodyParam.get("userId"))
                .map(RequestBody::getContentString)
                .orElseThrow(UserExceptionConverter::notFoundUser));
        if (user == null) throw UserExceptionConverter.notFoundUser();
        if (reqPassword.compareTo(user.getPassword()) != 0) throw UserExceptionConverter.unAuthorized();

        return Auth.addSession(user);
    }

    public User getUser(Request request) {
        String cookie = request.header.get(Config.HEADER_COOKIE);
        if (cookie == null) return null;
        String SID = Utils.getRestStr(cookie, "=", 1);
        return Auth.getSession(SID);
    }

    public User getUserOrException(Request request) {
        User user = getUser(request);
        if (user == null) throw UserExceptionConverter.needToLogin();
        return user;
    }

    public void deleteUserSession(Request request) {
        String cookie = request.header.get(Config.HEADER_COOKIE);
        if (cookie == null) return;
        String SID = Utils.getRestStr(cookie, "=", 1);

        Auth.deleteSession(SID);
    }

    public void updateUser(Request request) {
        User user = getUserOrException(request);
        if (request.bodyParam.get("userName") != null && !request.bodyParam.get("userName").toString().isBlank()) {
            String userName = request.bodyParam.get("userName").getContentString().trim();
            if (userName.length() < Config.MIN_USER_DATA_LENGTH) throw UserExceptionConverter.tooShortUserName();
            user.setName(userName);
        }

        if (request.bodyParam.get("password") != null && request.bodyParam.get("checkPassword") != null
        && !request.bodyParam.get("password").toString().isBlank()) {
            String password = request.bodyParam.get("password").getContentString().trim();
            String checkPassword = request.bodyParam.get("checkPassword").getContentString().trim();
            if(password.compareTo(checkPassword) != 0) throw UserExceptionConverter.passwordNotMatch();
            if (password.length() < Config.MIN_USER_DATA_LENGTH)
                throw UserExceptionConverter.tooShortUserPassword();
            user.setPassword(password);
        }

        RequestBody image = request.bodyParam.get("profileImage");
        RequestBody imagePath = request.bodyParam.get("previewImg");
        if(image != null) {
            checkImageType(image.getContent());
            switchProfileImage(image.getContent(), user);
        }
        else if(imagePath != null){
            try {
                String raw = imagePath.getContentString();
                String path = new java.net.URL(raw).getPath();

                if (Config.IMAGE_DEFAULT_PROFILE_API.equals(path)) {
                    switchProfileImage(
                            ImageManager.readImageProfile(Config.IMAGE_DEFAULT_PROFILE_NAME),
                            user
                    );
                }
            } catch (Exception e) {
            }
        }
        Database.updateUser(user);
    }

    private void switchProfileImage(byte[] image, User user) {
        if(!user.getImagePath().endsWith(Config.IMAGE_DEFAULT_PROFILE_NAME)){
            ImageManager.deleteProfileFile(user.getImagePath());
        }
        String path = ImageManager.saveImageProfile(image);
        user.setImagePath(path);
    }
}

