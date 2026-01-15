package webserver.parse.DTO;

import model.Post;
import model.User;

public record PostViewer(String authorName, String authorImagePath, String imagePath, int likes, int commentNum, String content, long postId) {

    public PostViewer(Post post, User user, int commentNum){
        this(user.getName(),
                user.getImagePath(),
                post.postImagePath(),
                post.likes(),
                commentNum,
                post.content(),
                post.postId()
                );
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
