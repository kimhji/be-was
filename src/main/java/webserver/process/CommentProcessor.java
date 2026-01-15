package webserver.process;

import customException.CommentExceptionConverter;
import customException.UserExceptionConverter;
import customException.WebException;
import db.Database;
import model.Comment;
import model.User;
import webserver.http.Request;
import webserver.http.RequestBody;
import webserver.http.Response;

import java.util.Optional;

public class CommentProcessor {
    public Response createComment(Request request, User user) {

        String[] pathSplit = request.path.split("/");
        if (request.bodyParam.getOrDefault("content", null) == null) {
            throw CommentExceptionConverter.badContentComment();
        }
        try {
            long postId = Long.parseLong(pathSplit[pathSplit.length - 1]);
            Database.addComment(new Comment(postId, user.getUserId(), request.bodyParam.get("content").toString()));
            return new Response(
                    WebException.HTTPStatus.CREATED,
                    null,
                    Response.ContentType.PLAIN_TEXT
            );
        } catch (NumberFormatException e) {
            throw CommentExceptionConverter.noPostId();
        }
    }

}
