package model;

public record Comment(long commentId, long postId, String authorId, String content) {
    Comment(long postId, String userId, String content) {
        this(0, postId, userId, content);
    }
}
