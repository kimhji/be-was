package db;

import customException.*;
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
                    "    name VARCHAR(50) NOT NULL,\n" +
                    "    email VARCHAR(100) NOT NULL\n" +
                    ");\n");

            stmt.execute("CREATE TABLE posts (\n" +
                    "    post_id BIGINT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    user_id VARCHAR(50) NOT NULL,\n" +
                    "    image_path VARCHAR(100),\n" +
                    "    content CLOB,\n" +
                    "    likes INT,\n" +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    ");\n");
        }
        catch (SQLException e){
            logger.error(e.getMessage());
        }
    }


    public static void addUser(User user) {
        try {
            String sql = "INSERT INTO users(user_id, password, name, email) VALUES (?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());

            pstmt.executeUpdate();
        }
        catch (SQLException e){
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
                    result.getString("name"),
                    result.getString("email")
            );
        }
        catch (SQLException e){
            throw DBExceptionConverter.failToAddUser();
        }
    }

    public static Collection<User> findAll() {
        try {
            Collection<User> users = new ArrayList<>();
            String sql = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                users.add(new User(result.getString("user_id"),
                        result.getString("password"),
                        result.getString("name"),
                        result.getString("email")));
            }
            return users;
        }
        catch (SQLException e){
            throw DBExceptionConverter.failToAddUser();
        }
    }


    public static void addPost(Post post) {
        try {

            String imagePath = ImageManager.saveImage(post.image());

            String sql = "INSERT INTO posts(user_id, image_path, content, likes) VALUES (?, ?, ?, 0)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, post.userId());
            pstmt.setString(2, imagePath);
            pstmt.setString(3, post.content());

            pstmt.executeUpdate();
        }
        catch (SQLException e){
            throw DBExceptionConverter.failToAddPost();
        }
    }

    public static Post getRecentPost(){
        String sql = """
            SELECT post_id, user_id, image_path, content
            FROM posts
            ORDER BY post_id DESC
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw PostExceptionConverter.notFoundPost();
            }

            return new Post(
                    rs.getLong("post_id"),
                    ImageManager.readImage(rs.getString("image_path")),
                    rs.getString("user_id"),
                    rs.getString("content"),
                    rs.getInt("likes")
            );

        } catch (SQLException e) {
            throw DBExceptionConverter.failToFindPost();
        }
    }
}
