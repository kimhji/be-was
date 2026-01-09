package common;

import model.User;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Auth {
    static Map<String, User> session = new HashMap<>();

    public static String addSession(User user) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        long value = (long) (random.nextDouble() * 1_000_000_000L);
        String keyData = String.format("%09d", value);
        while (session.containsKey(keyData)) {
            value = (long) (random.nextDouble() * 1_000_000_000L);
            keyData = String.format("%09d", value);
        }
        session.put(keyData, user);
        return keyData;
    }

    public static User getSession(String keyData) {
        return session.get(keyData);
    }

    public static void deleteSession(String key) {
        if (key == null) return;
        session.remove(key);
    }
}
