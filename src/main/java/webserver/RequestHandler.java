package webserver;

import java.io.*;
import java.net.Socket;

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
        SimpleReq req = new SimpleReq(SimpleReq.Method.GET, "/registration");
        SimpleReq res = new SimpleReq(SimpleReq.Method.GET, "/registration/index.html");
        router.register(req, K ->
                {
                    try {
                        return Response.processReq(res);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        router.register(new SimpleReq(SimpleReq.Method.GET, "/create"), value-> new User(value.queryParam.get("userId"), value.queryParam.get("password"),value.queryParam.get("name"),value.queryParam.get("email"))
                .toString().getBytes());
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            String req = getReq(in);
            logger.debug(req);

            SimpleReq simpleReq = new SimpleReq(req);
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = router.route(simpleReq);
            if(body == null) {
                if(simpleReq.method == SimpleReq.Method.GET){
                    body = Response.processReq(simpleReq);
                }
                else{
                    body = "".getBytes();
                }
                response200HeaderByType(dos, body.length, simpleReq);
            }
            else{
                response200Header(dos, body.length);
            }
            responseBody(dos, body);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200HeaderByType(DataOutputStream dos, int lengthOfBodyContent, SimpleReq simpleReq) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+contentType(simpleReq.path)+"\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String contentType(String path) {
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
