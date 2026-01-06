package webserver;

import java.io.*;
import java.net.Socket;

import customException.WebException;
import customException.WebStatusConverter;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static Router router = new Router();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public static void init() {
        router.register(new SimpleReq(SimpleReq.Method.GET, "/registration"), (K) ->
                {
                    SimpleReq realReq = new SimpleReq(SimpleReq.Method.GET, "/registration/index.html");
                    byte[] body = StaticFileProcessor.processReq(realReq);
                    if(body == null) throw WebStatusConverter.inexistenceStaticFile();
                    return new Response(WebException.HTTPStatus.OK, body, contentType(realReq.path));
                }
        );
        router.register(new SimpleReq(SimpleReq.Method.GET, "/create"), value-> {
            byte[] body = new User(value.queryParam.get("userId"), value.queryParam.get("password"),value.queryParam.get("name"),value.queryParam.get("email"))
                    .toString().getBytes();
            return new Response(WebException.HTTPStatus.OK, body, "text/html;charset=utf-8");
        });

        router.register(new SimpleReq(SimpleReq.Method.GET, "/"), dummy->{
            return new Response(WebException.HTTPStatus.OK,"<h1>Hello World</h1>".getBytes(), "text/html;charset=utf-8");

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
                    if(body != null) {
                        response = new Response(WebException.HTTPStatus.OK, body, contentType(simpleReq.path));
                    }
                }
                if (response == null) {
                    response = router.route(simpleReq);
                }

                responseHeaderByStatusAndType(dos, response.body.length, response.statusCode, response.contentType);

                responseBody(dos, response.body);
            }
            catch (WebException e){
                byte[] body = e.getMessage()
                        .getBytes();

                responseHeaderByStatusAndType(
                        dos,
                        body.length,
                        e.statusCode,
                        "text/plain; charset=utf-8"
                );
                responseBody(dos, body);
            }
        }
        catch (IOException e) {
            logger.debug("Connection closed", e);
        } catch (Exception e) {
            logger.error("Unhandled error", e);
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        responseHeaderByStatusAndType(dos, lengthOfBodyContent, WebException.HTTPStatus.OK, "text/html;charset=utf-8");
    }

    private void response200HeaderByType(DataOutputStream dos, int lengthOfBodyContent, SimpleReq simpleReq) {
        responseHeaderByStatusAndType(dos, lengthOfBodyContent, WebException.HTTPStatus.OK, contentType(simpleReq.path));
    }

    private void responseHeaderByStatusAndType(DataOutputStream dos, int lengthOfBodyContent, WebException.HTTPStatus status, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 "+status.getHttpStatus()+" "+status.name()+" \r\n");
            dos.writeBytes("Content-Type: "+contentType+"\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static String contentType(String path) {
        if (path.endsWith(".html")) return "text/html;charset=utf-8";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String getReq(InputStream in) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        String req = "";
        while(line != null && !line.isEmpty()){
            req += line + "\n";
            line = br.readLine();
        }
        return req;
    }

}
