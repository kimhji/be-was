package webserver.process;

import common.Config;
import customException.CommentExceptionConverter;
import customException.PostExceptionConverter;
import customException.WebException;
import customException.WebStatusConverter;
import db.Database;
import db.ImageManager;
import model.Comment;
import model.Post;
import model.User;
import webserver.http.Request;
import webserver.http.RequestBody;
import webserver.http.Response;
import webserver.parse.DTO.PostViewer;
import webserver.parse.PageReplacer;
import webserver.parse.DataReplacer;
import webserver.parse.PageStruct;
import webserver.route.Router;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Processor {

    private static final Router router = new Router();
    private static final UserProcessor userProcessor = new UserProcessor();
    private static final DataReplacer pageReplacer = new DataReplacer("page");
    private static final DataReplacer userReplacer = new DataReplacer("user");
    private static final DataReplacer postReplacer = new DataReplacer("post");
    private static final PageStruct pageStruct = new PageStruct();
    //private static final PageReplacer pageReplacer = new PageReplacer();

    public Processor() {
        init();
    }

    public void init() {
        router.register(new Request(Request.Method.GET, "/registration"), (request) ->
                {
                    request.path = Config.REGISTRATION_PAGE_PATH;
                    return process(request);
                }
        );
        router.register(new Request(Request.Method.GET, "/login"), (request) ->
                {
                    request.path = Config.LOGIN_PAGE_PATH;
                    if (userProcessor.getUser(request) != null) {
                        request.path = Config.DEFAULT_PAGE_PATH;
                    }
                    return process(request);
                }
        );
        router.register(new Request(Request.Method.GET, "/mypage"), (request) ->
                {
                    request.path = Config.MY_PAGE_PAGE_PATH;
                    return process(request);
                }
        );
        router.register(new Request(Request.Method.GET, "/write"), request -> {
            request.path = Config.ARTICLE_PAGE_PATH;
            return process(request);
        });

        router.register(new Request(Request.Method.GET, "/"), request -> {
            request.path = Config.DEFAULT_PAGE_PATH;
            return process(request);
        });

        router.register(new Request(Request.Method.POST, "/user/create"), request -> {
            byte[] body = userProcessor.createUser(request);
            Response response = new Response(WebException.HTTPStatus.MOVED_TEMPORALLY, body, Response.ContentType.HTML);
            response.addHeader(Config.HEADER_LOCATION, "http://localhost:8080/index.html");
            return response;
        });

        router.register(new Request(Request.Method.POST, "/user/login"), request -> {
            String token = userProcessor.loginUser(request);
            Response response = new Response(WebException.HTTPStatus.MOVED_TEMPORALLY, null, Response.ContentType.HTML);
            response.addHeader(Config.HEADER_LOCATION, "http://localhost:8080/index.html");
            response.addHeader(Config.HEADER_SET_COOKIE, "SID=" + token + "; Path=/");
            return response;
        });

        router.register(new Request(Request.Method.POST, "/user/logout"), request -> {
            userProcessor.deleteUserSession(request);
            Response response = new Response(WebException.HTTPStatus.OK, null, Response.ContentType.HTML);
            response.addHeader(Config.HEADER_LOCATION, "http://localhost:8080/index.html");
            response.addHeader(Config.HEADER_SET_COOKIE, "cookieName=; Max-Age=0;");
            return response;
        });

        router.register(new Request(Request.Method.POST, "/post/create"), request -> {
            User user = userProcessor.getUserOrException(request);
            if (request.bodyParam.getOrDefault("content", null) == null) {
                throw PostExceptionConverter.badContentPost();
            }
            Post post = new Post(request.bodyParam.getOrDefault("image", new RequestBody("")).getContent(),
                    user.getUserId(),
                    request.bodyParam.get("content").toString());
            Database.addPost(post);
            return new Response(WebException.HTTPStatus.OK, null, Response.ContentType.PLAIN_TEXT);
        });

        router.register(new Request(Request.Method.GET, "/image/profile"), request -> {
            String[] pathSplit = request.path.split("/");
            byte[] image = ImageManager.readImageProfile(pathSplit[pathSplit.length - 1]);
            System.out.println("profile" + pathSplit[pathSplit.length - 1] + " is OK.");
            return new Response(WebException.HTTPStatus.OK, image, Response.contentType(request.path));
        });

        router.register(new Request(Request.Method.GET, "/image"), request -> {
            String[] pathSplit = request.path.split("/");
            byte[] image = ImageManager.readImagePost(pathSplit[pathSplit.length - 1]);
            System.out.println("post" + pathSplit[pathSplit.length - 1] + " is OK.");
            return new Response(WebException.HTTPStatus.OK, image, Response.contentType(request.path));
        });


        router.register(new Request(Request.Method.POST, "/post/like"), request -> {
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
        });

        router.register(new Request(Request.Method.GET, "/comment"), request -> {
            String[] pathSplit = request.path.split("/");
            try {
                request.path = Config.COMMENT_PAGE_PATH;
                request.queryParam.put(Config.POST_ID_QUERY_NAME, pathSplit[pathSplit.length - 1]);
                return process(request);
            } catch (NumberFormatException e) {
                throw CommentExceptionConverter.noPostId();
            }
        });

        router.register(new Request(Request.Method.POST, "/comment"), request -> {
            String[] pathSplit = request.path.split("/");
            User user = userProcessor.getUserOrException(request);
            if (request.bodyParam.getOrDefault("content", null) == null) {
                throw CommentExceptionConverter.badContentComment();
            }
            try {
                long postId = Long.parseLong(pathSplit[pathSplit.length - 1]);
                Database.addComment(new Comment(postId, user.getUserId(), request.bodyParam.get("content").toString()));
                Response response = new Response(
                        WebException.HTTPStatus.CREATED,
                        null,
                        Response.ContentType.PLAIN_TEXT
                );
                //response.addHeader(Config.HEADER_LOCATION, Config.POST_PAGE_PATH + "/" + postId);
                return response;
            } catch (NumberFormatException e) {
                throw CommentExceptionConverter.noPostId();
            }
        });
    }

    public Response process(Request simpleReq) {
        Response response = null;
        User user = (Router.needLogin(simpleReq.path)) ?
                userProcessor.getUserOrException(simpleReq)
                : userProcessor.getUser(simpleReq);
        if (simpleReq.method == Request.Method.GET) {
            byte[] body = StaticFileProcessor.processReq(simpleReq);

            if (body != null) {
                pageStruct.setState(simpleReq.path, user != null);
                String template = pageReplacer.replace(pageStruct, new String(body));
                template = userReplacer.replace(user, template);
                PostViewer postViewer = getPostViewer(simpleReq);
                body = postReplacer.replace(postViewer, template).getBytes();
                if (postViewer == null && Router.needPostData(simpleReq.path)) {
                    body = getNoPostExceptionPage(simpleReq, user);
                }

                response = new Response(WebException.HTTPStatus.OK, body, Response.contentType(simpleReq.path));
            }
        }
        if (response == null) {
            response = router.route(simpleReq);
        }
        return response;
    }

    private byte[] getNoPostExceptionPage(Request request, User user) {
        request.path = Config.NOPOST_PAGE_PATH;
        byte[] body = StaticFileProcessor.processReq(request);
        if (body == null) throw WebStatusConverter.cannotFindNoPostPage();

        String template = pageReplacer.replace(pageStruct, new String(body));
        return userReplacer.replace(user, template).getBytes(StandardCharsets.UTF_8);
    }

    private PostViewer getPostViewer(Request request) {
        Post post = null;
        if (request.queryParam.get(Config.POST_ID_QUERY_NAME) != null) {
            long postId = Long.parseLong(request.queryParam.get(Config.POST_ID_QUERY_NAME));
            post = Database.getPostByPostId(postId);
        } else {
            post = Database.getRecentPost();
        }
        if (post == null) return null;
        User author = Database.findUserById(post.userId());
        if (author == null) return null;
        return new PostViewer(post, author, Database.findCommentsByPost(post.postId()).size());
    }
}
