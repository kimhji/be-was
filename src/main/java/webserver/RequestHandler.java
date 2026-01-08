package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import customException.UserExceptionConverter;
import customException.WebException;
import customException.WebStatusConverter;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.parser.Replacer;
import webserver.process.StaticFileProcessor;
import webserver.process.UserProcessor;
import webserver.route.Router;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final Router router = new Router();
    private static final UserProcessor userProcessor = new UserProcessor();
    private static final Replacer userReplacer = new Replacer("user");

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

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

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            try {
                Request simpleReq = getReq(in);
                logger.debug(simpleReq.toString());
                Response response = null;
                User user = userProcessor.getUser(simpleReq);
                if(Router.needLogin(simpleReq.path) && user == null) throw UserExceptionConverter.needToLogin();
                if (simpleReq.method == Request.Method.GET) {
                    byte[] body = StaticFileProcessor.processReq(simpleReq);

                    if (body != null) {
                        body = StaticFileProcessor.addUserData(body, user != null);
                        body = userReplacer.replace(user, new String(body)).getBytes();

                        response = new Response(WebException.HTTPStatus.OK, body, Response.contentType(simpleReq.path));
                    }
                }
                if (response == null) {
                    response = router.route(simpleReq);
                }

                response.setResponseHeader(dos);

                response.responseBody(dos);
            } catch (WebException e) {
                Response response = new Response(e.statusCode, e.getMessage().getBytes(), Response.ContentType.PLAIN_TEXT);
                response.setResponseHeader(dos);
                if(e.path != null && !e.path.isBlank())
                    response.addHeader("Location", e.path);
                response.responseBody(dos);
            }
        } catch (IOException e) {
            logger.debug("Connection closed", e);
        } catch (Exception e) {
            logger.error("Unhandled error", e);
        }
    }

    private Request getReq(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
        );

        String line;
        StringBuilder headerPart = new StringBuilder();

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            headerPart.append(line).append("\r\n");
        }
        Request req = new Request(headerPart.toString());

        String contentLength = req.header.get("Content-Length");
        if (contentLength != null) {
            int len = Integer.parseInt(contentLength);

            if(len <= 0) return req;

            char[] bodyChars = new char[len];
            int read = 0;
            while (read < len) {
                read += br.read(bodyChars, read, len - read);
            }

            String body = new String(bodyChars);
            req.addBodyParam(body);
        }

        return req;
    }


}
