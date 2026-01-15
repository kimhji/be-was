package webserver.process;

import common.Auth;
import common.Config;
import common.Utils;
import customException.UserExceptionConverter;
import db.Database;
import model.User;
import webserver.http.Request;
import webserver.http.RequestBody;

import java.util.Optional;

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
}

