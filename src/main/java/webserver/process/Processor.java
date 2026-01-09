package webserver.process;

import customException.UserExceptionConverter;
import customException.WebException;
import customException.WebStatusConverter;
import model.User;
import webserver.http.Request;
import webserver.http.Response;
import webserver.parse.PageReplacer;
import webserver.parse.Replacer;
import webserver.route.Router;

public class Processor {

    private static final Router router = new Router();
    private static final UserProcessor userProcessor = new UserProcessor();
    private static final Replacer userReplacer = new Replacer("user");
    private static final PageReplacer pageReplacer = new PageReplacer();


    public static void init() {
        router.register(new Request(Request.Method.GET, "/registration"), (K) ->
                {
                    Request realReq = new Request(Request.Method.GET, "/registration/index.html");
                    byte[] body = StaticFileProcessor.processReq(realReq);
                    if (body == null) throw WebStatusConverter.inexistenceStaticFile();
                    return new Response(WebException.HTTPStatus.OK, body, Response.contentType(realReq.path));
                }
        );
        router.register(new Request(Request.Method.GET, "/login"), (K) ->
                {
                    Request realReq = new Request(Request.Method.GET, "/login/index.html");
                    byte[] body = StaticFileProcessor.processReq(realReq);
                    if (body == null) throw WebStatusConverter.inexistenceStaticFile();
                    return new Response(WebException.HTTPStatus.OK, body, Response.contentType(realReq.path));
                }
        );
        router.register(new Request(Request.Method.GET, "/mypage"), (K) ->
                {
                    Request realReq = new Request(Request.Method.GET, "/mypage/index.html");
                    byte[] body = StaticFileProcessor.processReq(realReq);
                    if (body == null) throw WebStatusConverter.inexistenceStaticFile();
                    return new Response(WebException.HTTPStatus.OK, body, Response.contentType(realReq.path));
                }
        );
//        router.register(new Request(Request.Method.GET, "/create"), value -> {
//            byte[] body = userProcessor.createUser(value);
//            return new Response(WebException.HTTPStatus.CREATED, body, Response.ContentType.HTML);
//        });

        router.register(new Request(Request.Method.GET, "/"), dummy -> {
            return new Response(WebException.HTTPStatus.OK, "<h1>Hello World</h1>".getBytes(), Response.ContentType.HTML);

        });

        router.register(new Request(Request.Method.POST, "/user/create"), request -> {
            byte[] body = userProcessor.createUser(request);
            Response response = new Response(WebException.HTTPStatus.MOVED_TEMPORALLY, body, Response.ContentType.HTML);
            response.addHeader("Location", "http://localhost:8080/index.html");
            return response;
        });

        router.register(new Request(Request.Method.POST, "/user/login"), request -> {
            String token = userProcessor.loginUser(request);
            Response response = new Response(WebException.HTTPStatus.MOVED_TEMPORALLY, null, Response.ContentType.HTML);
            response.addHeader("Location", "http://localhost:8080/index.html");
            response.addHeader("set-cookie", "SID=" + token + "; Path=/");
            return response;
        });
    }

    public Response process(Request simpleReq){
        Response response = null;
        User user = userProcessor.getUser(simpleReq);
        if(Router.needLogin(simpleReq.path) && user == null) throw UserExceptionConverter.needToLogin();
        if (simpleReq.method == Request.Method.GET) {
            byte[] body = StaticFileProcessor.processReq(simpleReq);

            if (body != null) {
                String template = pageReplacer.getWholePage(new String(body), simpleReq.path, user!=null);
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
