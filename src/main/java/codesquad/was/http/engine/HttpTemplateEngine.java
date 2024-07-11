package codesquad.was.http.engine;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpTemplateEngine {
    private static final Pattern IF_PATTERN = Pattern.compile("\\{\\{if(.*?)then(.*?)else(.*?)}}",Pattern.DOTALL);
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(.*?)}}");

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

    public static String render(String template, Map<String, Object> context) {
        Deque<StringBuilder> stack = new LinkedList<>();
        char[] array = template.toCharArray();
        StringBuilder result = new StringBuilder();
        for(int i=0;i<array.length;i++){
            if(array[i]=='{' && i+1 < array.length && array[i+1] == '{'){
                stack.offerLast(new StringBuilder().append("{{"));
                i++;
            }
            else if(array[i] == '}' && i+1 < array.length && array[i+1] == '}'){
                if(stack.isEmpty()){
                    result.append(array[i]);
                }
                else{
                    stack.peekLast().append("}}");
                    StringBuilder pop = stack.pollLast();
                    String popped = pop.toString();
                    String innerRendered = innerRender(popped,context);
                    if(stack.isEmpty()){
                        result.append(innerRendered);
                    }
                    else{
                        stack.peekLast().append(innerRendered);
                    }
                    i++;
                }
            }
            else{
                if(stack.isEmpty()){
                    result.append(array[i]);
                }
                else{
                    stack.peekLast().append(array[i]);
                }
            }
        }
        while(!stack.isEmpty()){
            result.append(stack.pollFirst().toString());
        }
        return result.toString();
    }

    private static String innerRender(String popped,Map<String,Object> context){
        Matcher ifMatcher = IF_PATTERN.matcher(popped);
        if (ifMatcher.find()) {
            String conditionExpression = ifMatcher.group(1).trim();
            String thenExpression = ifMatcher.group(2).trim();
            String elseExpression = ifMatcher.group(3).trim();

            boolean isConditionTrue = evaluateCondition(conditionExpression, context);
            String replacement = isConditionTrue ? thenExpression : elseExpression;

            return replacement;
        } else {
            Matcher variableMatcher = VARIABLE_PATTERN.matcher(popped);
            if (variableMatcher.find()) {
                String key = variableMatcher.group(1).trim();
                Object value = context.get(key);
                if (value != null) {
                    return variableMatcher.replaceFirst(value.toString());
                } else {
                    // If key not found, replace with empty string
                    return variableMatcher.replaceFirst("null");
                }
            } else {
                // No more patterns found, append to result
                return "";
            }
        }
    }
}
