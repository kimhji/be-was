package webserver.process;

import common.Config;
import customException.PostExceptionConverter;
import customException.WebException;
import db.Database;
import model.Post;
import model.User;
import webserver.http.Request;
import webserver.http.RequestBody;
import webserver.http.Response;
import webserver.parse.PageReplacer;
import webserver.parse.DataReplacer;
import webserver.route.Router;

public class Processor {

    private static final Router router = new Router();
    private static final UserProcessor userProcessor = new UserProcessor();
    private static final DataReplacer userReplacer = new DataReplacer("user");
    private static final PageReplacer pageReplacer = new PageReplacer();

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
            return response;
        });

        router.register(new Request(Request.Method.POST, "/post/create"), request -> {
            User user = userProcessor.getUserOrException(request);
            if(request.bodyParam.getOrDefault("content", null)==null){
                throw PostExceptionConverter.badContentPost();
            }
            Post post = new Post(request.bodyParam.getOrDefault("image", new RequestBody("")).getContent(),
                    user.getUserId(),
                    request.bodyParam.get("content").toString());
            Database.addPost(post);
            return new Response(WebException.HTTPStatus.OK, null, Response.ContentType.PLAIN_TEXT);
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
                String template = pageReplacer.getWholePage(new String(body), simpleReq.path, user != null);
                body = userReplacer.replace(user, template).getBytes();

                response = new Response(WebException.HTTPStatus.OK, body, Response.contentType(simpleReq.path));
            }
        }
        if (response == null) {
            response = router.route(simpleReq);
        }
        return response;
    }
}
