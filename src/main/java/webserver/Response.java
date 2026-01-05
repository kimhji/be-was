package webserver;

import customException.WebStatusConverter;

import java.io.File;
import java.io.FileInputStream;

public class Response {
    static byte[] processReq(SimpleReq simpleReq){
        byte[] result = "".getBytes();
        switch(simpleReq.method){
            case GET:
                result = getStaticSources(simpleReq.path);
                break;
            default:
                break;
        }
        return result;
    }

    static private byte[] getStaticSources(String path){
        if(path.compareTo("/")==0) return "<h1>Hello World</h1>".getBytes();

        String wholePath = "./src/main/resources/static"+path;
        File file = new File(wholePath);
        if(!file.exists()) throw WebStatusConverter.inexistenceStaticFile();
        try {
            FileInputStream fr = new FileInputStream(wholePath);

            byte[] buffer = fr.readAllBytes();
            return buffer;
        }
        catch (Exception e) {
            throw WebStatusConverter.fileReadError();
        }
    }
}
