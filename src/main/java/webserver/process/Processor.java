package webserver.process;

import common.Config;
import common.Utils;
import customException.*;
import db.Database;
import db.ImageManager;
import model.Comment;
import model.Post;
import model.User;
import webserver.http.Request;
import webserver.http.RequestBody;
import webserver.http.Response;
import webserver.parse.DTO.CommentViewer;
import webserver.parse.DTO.CursorViewer;
import webserver.parse.DTO.PostViewer;
import webserver.parse.PageReplacer;
import webserver.parse.DataReplacer;
import webserver.parse.PageStruct;
import webserver.parse.RepeatDataReplacer;
import webserver.route.Router;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static webserver.process.StaticFileProcessor.checkImageType;

public class Processor {

    private static final Router router = new Router();
    private static final UserProcessor userProcessor = new UserProcessor();
    private static final CommentProcessor commentProcessor = new CommentProcessor();
    private static final PostProcessor postProcessor = new PostProcessor();
    private static final DataReplacer pageReplacer = new DataReplacer("page");
    private static final DataReplacer userReplacer = new DataReplacer("user");
    private static final DataReplacer postReplacer = new DataReplacer("post");
    private static final DataReplacer cursorReplacer = new DataReplacer("cursor");
    private static final PageStruct pageStruct = new PageStruct();
    private static final RepeatDataReplacer commentRepeatReplacer = new RepeatDataReplacer("comment", Config.COMMENT_REPEAT_FORMAT, Config.NO_COMMENT);

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
                        request.path = Config.MAIN_PAGE_PATH;
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
            request.path = Config.MAIN_PAGE_PATH;
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


        router.register(new Request(Request.Method.POST, "/user/update"), request -> {
            userProcessor.updateUser(request);
            return new Response(WebException.HTTPStatus.OK, null, Response.ContentType.HTML);
        });

        router.register(new Request(Request.Method.POST, "/post/create"), request -> {
            User user = userProcessor.getUserOrException(request);
            if (request.bodyParam.get("image") == null) {
                throw PostExceptionConverter.badFileContentPost();
            }
            checkImageType(request.bodyParam.get("image").getContent());
            Post post = new Post(request.bodyParam.get("image").getContent(),
                    user.getUserId(),
                    request.bodyParam.getOrDefault("content", new RequestBody("")).toString());
            Database.addPost(post);
            return new Response(WebException.HTTPStatus.OK, null, Response.ContentType.PLAIN_TEXT);
        });

        router.register(new Request(Request.Method.GET, "/image/profile"), request -> {
            String[] pathSplit = request.path.split("/");
            byte[] image = ImageManager.readImageProfile(pathSplit[pathSplit.length - 1]);
            return new Response(WebException.HTTPStatus.OK, image, Response.contentType(request.path));
        });

        router.register(new Request(Request.Method.GET, "/image"), request -> {
            String[] pathSplit = request.path.split("/");
            byte[] image = ImageManager.readImagePost(pathSplit[pathSplit.length - 1]);
            return new Response(WebException.HTTPStatus.OK, image, Response.contentType(request.path));
        });


        router.register(new Request(Request.Method.POST, "/post/like"), postProcessor::addLikeToPost);

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
            User user = userProcessor.getUserOrException(request);
            return commentProcessor.createComment(request, user);
        });

        router.register(new Request(Request.Method.GET, "/post"), request -> {
            String[] pathSplit = request.path.split("/");
            try {
                request.path = Config.MAIN_PAGE_PATH;
                if(Database.getPostByPostId(Long.parseLong(pathSplit[pathSplit.length - 1])) != null)
                    request.queryParam.put(Config.POST_ID_QUERY_NAME, pathSplit[pathSplit.length - 1]);

                return process(request);
            }
            catch (NumberFormatException e) {
                throw PostExceptionConverter.badPostId();
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
                if (Router.needPostData(simpleReq.path)) {
                    PostViewer postViewer = getPostViewer(simpleReq);
                    if(postViewer == null){
                        template = new String(getNoPostExceptionPage(simpleReq, user));
                    }
                    else if(Router.needCommentData(simpleReq.path)){
                        Collection<CommentViewer> comments = getCommentViewers(postViewer);
                        String queryP = simpleReq.queryParam.get("expanded");
                        boolean expanded = queryP != null && queryP.equals("true");

                        StringBuilder sb = new StringBuilder(template);
                        Utils.replaceAll(sb, "{{expand_comment_btn}}",
                                (!expanded && postViewer.commentNum()>Config.DEFAULT_COMMENT_COUNT)?Config.COMMENT_WANT_TO_SEE_MORE:"");
                        template = sb.toString();
                        if(!expanded)
                            template = commentRepeatReplacer.repeatReplace(comments, template, Config.DEFAULT_COMMENT_COUNT);
                        else
                            template = commentRepeatReplacer.repeatReplace(comments, template);

                        template = cursorReplacer.replace(getCursorViewer(postViewer.postId()), template);
                    }
                    template = postReplacer.replace(postViewer, template);
                }
                template = userReplacer.replace(user, template);
                body = template.getBytes(StandardCharsets.UTF_8);

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

    private Collection<CommentViewer> getCommentViewers(PostViewer postViewer){
        Collection<Comment> comments = Database.findCommentsByPost(postViewer.postId());
        return comments.stream().map(comment -> {
            User user = Database.findUserById(comment.authorId());
            if(user == null) throw UserExceptionConverter.notFoundUser();
            return new CommentViewer(comment.content(), user.getName(), user.getImagePath());
        }).toList();
    }

    private CursorViewer getCursorViewer(long postId){
        Long prevId = Database.getPrevPostId(postId);
        Long nextId = Database.getNextPostId(postId);
        return new CursorViewer(prevId==null?"disabled":"", nextId==null?"disabled":"", prevId==null?"":String.valueOf(prevId), nextId==null?"":String.valueOf(nextId));
    }
}
