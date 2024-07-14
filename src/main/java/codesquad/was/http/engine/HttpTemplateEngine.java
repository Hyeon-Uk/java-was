package codesquad.was.http.engine;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpTemplateEngine {
    private static final Pattern VALUE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");
    private static final Pattern IF_ELSE_PATTERN = Pattern.compile("\\{\\{if\\s+(.+?)\\s+then\\s+(.+?)\\s+else\\s+(.+?)}}",Pattern.DOTALL);
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");
    private static final Pattern FOR_LOOP_PATTERN = Pattern.compile("\\{\\{for\\s+(\\w+)\\s+in\\s+\\{\\{(\\w+)}}\\s+(.+?)}}",
            Pattern.DOTALL);
    private static final Pattern OBJECT_PROPERTY_PATTERN = Pattern.compile("\\(([\\w.]+)\\)");


    public static String render(String template, Map<String, Object> context) throws IllegalAccessException {
        String result = renderForLoops(template, context);
        String result2 = replaceValues(result, context);
        String result3 = processIfElse(result2, context);
        return renderPlaceholders(result3,context);
    }

    private static String replaceValues(String template, Map<String, Object> context) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = VALUE_PATTERN.matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = context.containsKey(key) ? context.get(key) == null ? "null" : context.get(key).toString() : "null";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String processIfElse(String template, Map<String, Object> context) throws IllegalAccessException {
        StringBuffer result = new StringBuffer();
        Matcher matcher = IF_ELSE_PATTERN.matcher(template);

        while (matcher.find()) {
            String condition = matcher.group(1);
            String thenExpression = matcher.group(2);
            String elseExpression = matcher.group(3);

            boolean conditionResult = evaluateCondition(condition, context);
            String replacement = conditionResult ? thenExpression : elseExpression;

            // Recursively process nested value replacements and if-else statements
            replacement = render(replacement, context);

            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static boolean evaluateCondition(String conditionExpression, Map<String, Object> context) {
        if (conditionExpression.contains("==")) {
            String[] parts = conditionExpression.split("==");
            String key1 = parts[0].trim();
            String key2 = parts[1].trim();
            return key1.equals(key2);
        }
        else if(conditionExpression.contains("!=")) {
            String[] parts = conditionExpression.split("!=");
            String key1 = parts[0].trim();
            String key2 = parts[1].trim();
            return !key1.equals(key2);
        }
        return false;
    }


    private static String renderForLoops(String template, Map<String, Object> context) throws IllegalAccessException {
        StringBuffer result = new StringBuffer();
        Matcher matcher = FOR_LOOP_PATTERN.matcher(template);

        while (matcher.find()) {
            String itemName = matcher.group(1);
            String listName = matcher.group(2);
            String expression = matcher.group(3);

            List<?> items = (List<?>) context.get(listName);
            if (items == null) {
                matcher.appendReplacement(result, "");
                continue;
            }

            StringBuilder loopResult = new StringBuilder();
            for (Object item : items) {
                Map<String, Object> loopContext = new HashMap<>(context);
                loopContext.put(itemName, item);
                String renderedExpression = renderObjectProperties(expression, loopContext);
                loopResult.append(renderedExpression);
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(loopResult.toString()));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String renderObjectProperties(String template, Map<String, Object> context) throws IllegalAccessException {
        StringBuffer result = new StringBuffer();
        Matcher matcher = OBJECT_PROPERTY_PATTERN.matcher(template);

        while (matcher.find()) {
            String propertyPath = matcher.group(1);
            String[] parts = propertyPath.split("\\.");
            Object value = context.get(parts[0]);
            for (int i = 1; i < parts.length && value != null; i++) {
                Field field = findField(value.getClass(), parts[i]);
                if (field != null) {
                    field.setAccessible(true);
                    value = field.get(value);
                } else {
                    value = null;
                    break;
                }
            }
            String replacement = (value != null) ? value.toString() : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            }
        }
        return null;
    }

    private static String renderPlaceholders(String template, Map<String, Object> context) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = context.get(key);
            String replacement = (value != null) ? value.toString() : "null";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
