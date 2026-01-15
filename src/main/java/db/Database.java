package db;

import customException.*;
import model.Comment;
import model.Post;
import model.User;

import java.awt.*;
import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static Connection conn;
    private static Statement stmt;
    public static void init(){
        try {
            conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
            stmt = conn.createStatement();

            // 테이블 생성
            stmt.execute("CREATE TABLE users (\n" +
                    "    user_id VARCHAR(50) PRIMARY KEY,\n" +
                    "    password VARCHAR(255) NOT NULL,\n" +
                    "    name VARCHAR(50) NOT NULL UNIQUE\n" +
                    ");\n");

            stmt.execute("CREATE TABLE posts (\n" +
                    "    post_id BIGINT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    user_id VARCHAR(50) NOT NULL,\n" +
                    "    image_path VARCHAR(100),\n" +
                    "    content CLOB,\n" +
                    "    likes INT,\n" +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    ");\n");


            stmt.execute("CREATE TABLE comments (\n" +
                    "    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    post_id BIGINT NOT NULL,\n" +
                    "    user_id VARCHAR(50) NOT NULL,\n" +
                    "    content CLOB,\n" +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    ");\n");
        }
        catch (SQLException e){
            logger.error(e.getMessage());
        }
    }


    public static void addUser(User user) {
        try {
            String sql = "INSERT INTO users(user_id, password, name) VALUES (?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());

            pstmt.executeUpdate();
        }
        catch (SQLException e){
            logger.error(e.getMessage());
            throw DBExceptionConverter.failToAddUser();
        }
    }

    public static User findUserById(String userId) {
        try {
            if (userId == null || userId.isBlank()) throw UserExceptionConverter.needUserId();
            String sql = "SELECT * FROM users WHERE user_id = (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            ResultSet result = pstmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new User(
                    result.getString("user_id"),
                    result.getString("password"),
                    result.getString("name")
            );
        }
        catch (SQLException e){
            logger.error(e.getMessage());
            throw DBExceptionConverter.failToAddUser();
        }
    }

    public static User findUserByName(String name) {
        try {
            if (name == null || name.isBlank()) throw UserExceptionConverter.needUserName();
            String sql = "SELECT * FROM users WHERE name = (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);

            ResultSet result = pstmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new User(
                    result.getString("user_id"),
                    result.getString("password"),
                    result.getString("name")
            );
        }
        catch (SQLException e){
            logger.error(e.getMessage());
            throw DBExceptionConverter.failToAddUser();
        }
    }

    public static Collection<User> findAllUser() {
        try {
            Collection<User> users = new ArrayList<>();
            String sql = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                users.add(new User(result.getString("user_id"),
                        result.getString("password"),
                        result.getString("name")));
            }
            return users;
        }
        catch (SQLException e){
            logger.error(e.getMessage());
            throw DBExceptionConverter.failToFindUser();
        }
    }


    public static void addPost(Post post) {
        try {

            String imagePath = ImageManager.saveImagePost(post.image());

            String sql = "INSERT INTO posts(user_id, image_path, content, likes) VALUES (?, ?, ?, 0)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, post.userId());
            pstmt.setString(2, imagePath);
            pstmt.setString(3, post.content());

            pstmt.executeUpdate();
        }
        catch (SQLException e){
            logger.error(e.getMessage());
            throw DBExceptionConverter.failToAddPost();
        }
    }

    public static Post getRecentPost(){
        String sql = """
            SELECT post_id, user_id, image_path, content, likes
            FROM posts
            ORDER BY post_id DESC
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return null;
            }
            String imagePath = rs.getString("image_path");
            return new Post(
                    rs.getLong("post_id"),
                    ImageManager.readImagePost(imagePath),
                    rs.getString("user_id"),
                    rs.getString("content"),
                    rs.getInt("likes"),
                    imagePath
            );

        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw DBExceptionConverter.failToFindPost();
        }
    }

    public static void addComment(Comment comment) {
        try {
            String sql = "INSERT INTO comments(post_id, user_id, content) VALUES (?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, comment.postId());
            pstmt.setString(2, comment.authorId());
            pstmt.setString(3, comment.content());

            pstmt.executeUpdate();
        }
        catch (SQLException e){
            logger.error(e.getMessage());
            throw DBExceptionConverter.failToAddComment();
        }
    }

    public static Collection<Comment> findCommentsByPost(long postId) {
        try {
            Collection<Comment> comments = new ArrayList<>();
            String sql = "SELECT * FROM comment WHERE post_id = (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, postId);

            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                comments.add(new Comment(result.getLong("comment_id"),
                        result.getLong("post_id"),
                        result.getString("user_id"),
                        result.getString("content")));
            }
            return comments;
        }
        catch (SQLException e){
            throw DBExceptionConverter.failToAddUser();
        }
    }
}
