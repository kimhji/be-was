package webserver.process;

import customException.UserExceptionConverter;
import db.Database;
import model.User;
import webserver.SimpleReq;

public class UserProcessor {
    public byte[] createUser(SimpleReq request){
        User user = new User(request.queryParam.get("userId"), request.queryParam.get("password"), request.queryParam.get("name"), request.queryParam.get("email"));

        if(Database.findUserById(user.getUserId() ) != null) throw UserExceptionConverter.conflictUser();
        Database.addUser(user);

        return user.toString().getBytes();
    }
}
