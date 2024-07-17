package codesquad.framework.resolver;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.framework.resolver.annotation.RequestParam;
import codesquad.framework.resolver.annotation.SessionParam;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.session.Session;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.function.Function;

@Coffee
public class ArgumentResolver {
    private Object getObjectField(Field field, Function<String, String> getValue) {
        Class<?> type = field.getType();
        try {
            if (type.equals(Object.class)) {//bias condition
                return new Object();
            }
            String value = getValue.apply(field.getName());
            if (type.equals(String.class)) {
                return value;
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                return Integer.parseInt(value);
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                return Long.parseLong(value);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return Double.parseDouble(value);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return Float.parseFloat(value);
            } else {
                //object type
                Field[] declaredFields = type.getDeclaredFields();
                Constructor<?> defaultConstructor = type.getConstructor();
                defaultConstructor.setAccessible(true);
                Object instance = defaultConstructor.newInstance();
                for (Field f : declaredFields) {
                    f.setAccessible(true);
                    Object fValue = getObjectField(f, getValue);
                    f.set(instance, fValue);
                }
                return instance;
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("There is no default constructor for " + field.getName());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T makeObject(Class<T> clazz, Function<String, String> getValue) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            for (Field f : clazz.getDeclaredFields()) {
                Object objectField = getObjectField(f, getValue);
                f.setAccessible(true);
                f.set(instance, objectField);
            }
            return clazz.cast(instance);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object resolveParam(Parameter param, HttpRequest req, HttpResponse res,Model model) {
        Class<?> type = param.getType();
        if(type.equals(HttpRequest.class)){
            return req;
        }
        if(type.equals(HttpResponse.class)){
            return res;
        }
        if(type.equals(Session.class)){
            SessionParam sessionAnnotation = param.getAnnotation(SessionParam.class);
            if(sessionAnnotation != null){
                return req.getSession(sessionAnnotation.create());
            }
            return req.getSession(false);
        }
        if(type.equals(Model.class)){
            return model;
        }

        if (param.isAnnotationPresent(RequestParam.class)) {
            RequestParam requestParam = param.getAnnotation(RequestParam.class);
            String requestName = requestParam.name();
            String value = req.getQueryString(requestName);
            if (type.equals(String.class)) {
                return value;
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                return Integer.parseInt(value);
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                return Long.parseLong(value);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return Double.parseDouble(value);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return Float.parseFloat(value);
            } else {
                Function<String, String> getValue = (key -> req.getQueryString(key));
                return makeObject(type, getValue);
            }
        }

        return null;
    }

    public Object[] resolveArguments(Method method, HttpRequest req, HttpResponse res, Model model) {
        Parameter[] parameters = method.getParameters();
        return Arrays.stream(parameters)
                .map(param -> resolveParam(param, req, res,model))
                .toArray(Object[]::new);
    }
}
