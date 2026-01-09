package webserver.parse;

import common.UtilFunc;

import java.lang.reflect.Field;

public class Replacer {
    String replacerName;

    public Replacer(String replacerName) {
        this.replacerName = replacerName;
    }

    public String replace(Object data, String template) {
        if (data == null) return template;
        StringBuilder sb = new StringBuilder(template);
        Class<?> clazz = data.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(data);
                if (value == null) continue;

                String placeholder = "{{" + this.replacerName + "." + field.getName() + "}}";
                UtilFunc.replaceAll(sb, placeholder, value.toString());

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
}
