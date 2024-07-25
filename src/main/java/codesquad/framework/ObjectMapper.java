package codesquad.framework;

import codesquad.framework.coffee.annotation.Coffee;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

@Coffee
public class ObjectMapper {

    public String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }

        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        }

        if (obj.getClass().isArray() || obj instanceof Collection) {
            return arrayToJson(obj);
        }

        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj);
        }

        return objectToJson(obj);
    }

    private String objectToJson(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = obj.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            try {
                String fieldName = field.getName();
                Object value = field.get(obj);

                sb.append("\"").append(fieldName).append("\":");
                sb.append(toJson(value));

                if (i < fields.length - 1) {
                    sb.append(",");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        sb.append("}");
        return sb.toString();
    }

    private String arrayToJson(Object obj) {
        StringBuilder sb = new StringBuilder("[");

        if (obj.getClass().isArray()) {
            Object[] array = toObjectArray(obj);
            for (int i = 0; i < array.length; i++) {
                sb.append(toJson(array[i]));
                if (i < array.length - 1) {
                    sb.append(",");
                }
            }
        } else if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            int i = 0;
            for (Object item : collection) {
                sb.append(toJson(item));
                if (i < collection.size() - 1) {
                    sb.append(",");
                }
                i++;
            }
        }

        sb.append("]");
        return sb.toString();
    }

    private String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append(toJson(entry.getKey())).append(":");
            sb.append(toJson(entry.getValue()));
            if (i < map.size() - 1) {
                sb.append(",");
            }
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private Object[] toObjectArray(Object array) {
        if (array instanceof Object[]) {
            return (Object[]) array;
        }
        int length = java.lang.reflect.Array.getLength(array);
        Object[] result = new Object[length];
        for (int i = 0; i < length; i++) {
            result[i] = java.lang.reflect.Array.get(array, i);
        }
        return result;
    }
}
