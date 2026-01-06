package webserver.process;

import common.Auth;
import customException.UserExceptionConverter;
import db.Database;
import model.User;
import webserver.Request;

public class UserProcessor {
    public byte[] createUser(Request request){
        User user = new User(request.bodyParam.get("userId"), request.bodyParam.get("password"), request.bodyParam.get("name"), request.bodyParam.get("email"));

        if(Database.findUserById(user.getUserId() ) != null) throw UserExceptionConverter.conflictUser();
        Database.addUser(user);

        return user.toString().getBytes();
    }

    public byte[] loginUser(Request request){
        String reqPassword = request.bodyParam.get("password");
        if(reqPassword == null || reqPassword.isBlank()) throw UserExceptionConverter.needUserData();
        User user = Database.findUserById(request.bodyParam.get("userId"));
        if(user == null) throw UserExceptionConverter.notFoundUser();
        if(reqPassword.compareTo(user.getPassword()) != 0) throw UserExceptionConverter.unAuthorized();

        return Auth.addSession(user).getBytes();
    }
}
