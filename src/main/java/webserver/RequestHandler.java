package webserver;

import java.io.*;
import java.net.Socket;

import customException.WebException;
import customException.WebStatusConverter;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.process.StaticFileProcessor;
import webserver.process.UserProcessor;
import webserver.route.Router;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final Router router = new Router();
    private static final UserProcessor userProcessor = new UserProcessor();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public static void init() {
        router.register(new SimpleReq(SimpleReq.Method.GET, "/registration"), (K) ->
                {
                    SimpleReq realReq = new SimpleReq(SimpleReq.Method.GET, "/registration/index.html");
                    byte[] body = StaticFileProcessor.processReq(realReq);
                    if (body == null) throw WebStatusConverter.inexistenceStaticFile();
                    return new Response(WebException.HTTPStatus.OK, body, Response.contentType(realReq.path));
                }
        );
        router.register(new SimpleReq(SimpleReq.Method.GET, "/create"), value -> {
            byte[] body = userProcessor.createUser(value);
            return new Response(WebException.HTTPStatus.CREATED, body, Response.ContentType.HTML);
        });

        router.register(new SimpleReq(SimpleReq.Method.GET, "/"), dummy -> {
            return new Response(WebException.HTTPStatus.OK, "<h1>Hello World</h1>".getBytes(), Response.ContentType.HTML);

        });

        router.register(new SimpleReq(SimpleReq.Method.POST, "/user/create"), request -> {
            byte[] body = userProcessor.createUser(request);
            return new Response(WebException.HTTPStatus.OK, body, Response.ContentType.HTML);
            //return new Response(WebException.HTTPStatus.CREATED, )
        });
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            try {
                String req = getReq(in);
                logger.debug(req);
                SimpleReq simpleReq = new SimpleReq(req);
                Response response = null;
                if (simpleReq.method == SimpleReq.Method.GET) {
                    byte[] body = StaticFileProcessor.processReq(simpleReq);
                    if (body != null) {
                        response = new Response(WebException.HTTPStatus.OK, body, Response.contentType(simpleReq.path));
                    }
                }
                if (response == null) {
                    response = router.route(simpleReq);
                }

                response.setResponseHeader(dos);

                responseBody(dos, response.body);
            } catch (WebException e) {
                Response response = new Response(e.statusCode, e.getMessage().getBytes(), Response.ContentType.PLAIN_TEXT);
                response.setResponseHeader(dos);
                responseBody(dos, response.body);
            }
        } catch (IOException e) {
            logger.debug("Connection closed", e);
        } catch (Exception e) {
            logger.error("Unhandled error", e);
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) throws IOException{
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    private String getReq(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        String req = "";
        while (line != null && !line.isEmpty()) {
            req += line + "\n";
            line = br.readLine();
        }
        return req;
    }

}
