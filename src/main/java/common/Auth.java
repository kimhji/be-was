package common;

import model.User;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Auth {
    //tatic String key = System.getenv("JWT_TOKEN");

    static Map<String, User> session = new HashMap<>();

    public static String addSession(User user){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        long value = (long)(random.nextDouble() * 1_000_000_000L);
        String keyData = String.format("%09d", value);
        while(session.containsKey(keyData)){
            value = (long)(random.nextDouble() * 1_000_000_000L);
            keyData = String.format("%09d", value);
        }
        session.put(keyData, user);
        return keyData;
    }

    public static User getSession(String keyData){
        return session.get(keyData);
    }

    public static void deleteSession(String key){
        if(key == null) return;
        session.remove(key);
    }

//    public static String aesCBCEncode(String plainText) throws Exception {
//
//        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//        IvParameterSpec IV = new IvParameterSpec(key.substring(0,16).getBytes());
//        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        c.init(Cipher.ENCRYPT_MODE, secretKey, IV);
//
//        byte[] encrpytionByte = c.doFinal(plainText.getBytes("UTF-8"));
//        return new String(encrpytionByte);
//    }
//
//
//    public static String aesCBCDecode(String encodeText) throws Exception {
//
//        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//        IvParameterSpec IV = new IvParameterSpec(key.substring(0,16).getBytes());
//
//        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//
//        c.init(Cipher.DECRYPT_MODE, secretKey, IV);
//
//        return new String(c.doFinal(encodeText.getBytes()), "UTF-8");
//    }
}
