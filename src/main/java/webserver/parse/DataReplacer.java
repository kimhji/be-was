package webserver.parse;

import common.Utils;

import java.lang.reflect.Field;

public class DataReplacer {
    String replacerName;

    public DataReplacer(String replacerName) {
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
                Utils.replaceAll(sb, placeholder, value.toString());

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
}
