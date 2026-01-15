package webserver.parse.DTO;

import model.Post;
import model.User;

public class PostViewer {
    String authorName;
    String authorImagePath;
    String imagePath;
    int likes;
    int commentNum;
    String content;
    long postId;

    public PostViewer(Post post, User user, int commentNum){
        this.authorImagePath = user.getImagePath();
        this.authorName = user.getName();
        this.imagePath = post.postImagePath();
        this.likes = post.likes();
        this.commentNum = commentNum;
        this.content = post.content();
        this.postId = post.postId();
    }

    @Override
    public String toString() {
        return """
        {
          "postId": %d,
          "authorName": "%s",
          "authorImagePath": "%s",
          "imagePath": "%s",
          "likes": %d,
          "commentNum": %d,
          "content": "%s"
        }
        """.formatted(
                postId,
                authorName,
                authorImagePath,
                imagePath,
                likes,
                commentNum,
                content.replace("\"", "\\\"")
        );
    }

}
