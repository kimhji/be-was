package webserver.process;

import customException.PostExceptionConverter;
import customException.WebException;
import db.Database;
import model.Post;
import webserver.http.Request;
import webserver.http.Response;

public class PostProcessor {
    public Response addLikeToPost(Request request) {
        String[] pathSplit = request.path.split("/");
        try {
            long postId = Long.parseLong(pathSplit[pathSplit.length - 1]);
            int likes = Database.updatePostLikes(postId);
            return new Response(
                    WebException.HTTPStatus.OK,
                    ("{\"likes\":" + likes + "}").getBytes(),
                    Response.ContentType.JSON
            );
        } catch (NumberFormatException e) {
            throw PostExceptionConverter.badPostId();
        }
    }
}
