package db;

import customException.UserExceptionConverter;
import model.Post;
import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, User> posts = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        if (userId == null || userId.isBlank()) throw UserExceptionConverter.needUserId();
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }


    public static void addPost(Post post) {
        posts.put(post.getPostId(), post);
    }
}
