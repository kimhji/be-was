package webserver;

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
        if(!file.exists()) throw new RuntimeException("해당 static 파일이 존재하지 않습니다.");
        try {
            FileInputStream fr = new FileInputStream(wholePath);

            byte[] buffer = fr.readAllBytes();
            return buffer;
        }
        catch (Exception e) {
            throw new RuntimeException("파일을 읽는 과정 중에 예상치 못한 에러가 발생했습니다.");
        }
    }
}
