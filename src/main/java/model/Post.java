package model;

public record Post(String postId, byte[] image, String userId, String content){
    public static int postNum = 0;

    public Post(byte[] image, String userId, String content) {
        this(String.valueOf(++postNum), image, userId, content);
    }
}
