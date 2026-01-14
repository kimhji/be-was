package model;

public record Post(long postId, byte[] image, String userId, String content){
    public Post(byte[] image, String userId, String content) {
        this(0L, image, userId, content);
    }
}
