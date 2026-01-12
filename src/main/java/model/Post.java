package model;

public class Post {
    String postId;
    byte[] image;
    String userId;
    String content;

    public byte[] getImage(){
        return image;
    }

    public String getUserId(){
        return userId;
    }

    public String getContent(){
        return content;
    }
}
