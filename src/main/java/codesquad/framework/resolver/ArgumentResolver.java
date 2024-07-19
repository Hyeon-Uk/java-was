package codesquad.framework.resolver;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.framework.resolver.annotation.RequestParam;
import codesquad.framework.resolver.annotation.SessionParam;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.request.Request;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.Response;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.session.Session;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.function.Function;

@Coffee
public class ArgumentResolver {
    private Object getObjectField(Field field, Request req,Response res,Model model,Function<String, String> getValue,Function<String,HttpFile> getFileValue) {
        Class<?> type = field.getType();
        try {
            if (type.equals(Object.class)) {//bias condition
                return new Object();
            }
            String value = getValue.apply(field.getName());
            if (type.equals(String.class)) {
                return value;
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                return Integer.parseInt(value == null ? "0" : value);
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                return Long.parseLong(value == null ? "0" : value);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return Double.parseDouble(value == null ? "0" : value);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return Float.parseFloat(value == null ? "0" : value);
            } else if(type.equals(HttpFile.class)){
                return getFileValue.apply(field.getName());
            } else if(type.equals(HttpRequest.class)) {
                return req;
            }else if(type.equals(HttpResponse.class)) {
                return res;
            }else if(type.equals(Model.class)){
                return model;
            } else if(type.equals(Session.class)){
                return req.getSession();
            }else {
                //object type
                Field[] declaredFields = type.getDeclaredFields();
                Constructor<?> defaultConstructor = type.getConstructor();
                defaultConstructor.setAccessible(true);
                Object instance = defaultConstructor.newInstance();
                for (Field f : declaredFields) {
                    f.setAccessible(true);
                    Object fValue = getObjectField(f,req,res,model, getValue,getFileValue);
                    f.set(instance, fValue);
                }
                return instance;
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("There is no default constructor for " + field.getName());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T makeObject(Class<T> clazz, Request req,Response res,Model model, Function<String, String> getQueryValue,Function<String,HttpFile> getFileValue) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            for (Field f : clazz.getDeclaredFields()) {
                Object objectField = getObjectField(f, req,res,model,getQueryValue,getFileValue);
                f.setAccessible(true);
                f.set(instance, objectField);
            }
            return clazz.cast(instance);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object resolveParam(Parameter param, Request req, Response res,Model model) {
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
                return Integer.parseInt(value == null ? "0" : value);
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                return Long.parseLong(value == null ? "0" : value);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return Double.parseDouble(value == null ? "0" : value);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return Float.parseFloat(value == null ? "0" : value);
            } else if(type.equals(HttpFile.class)){
                return req.getFile(value);
            } else if(type.equals(HttpRequest.class)) {
                return req;
            }else if(type.equals(HttpResponse.class)) {
                return res;
            }else if(type.equals(Model.class)){
                return model;
            }else if(type.equals(Session.class)){
                return req.getSession();
            }else {
                Function<String, String> getQueryValue = (key -> req.getQueryString(key));
                Function<String, HttpFile> getFileValue = (key -> req.getFile(key));
                return makeObject(type, req,res,model,getQueryValue,getFileValue);
            }
        }

        return null;
    }

    public Object[] resolveArguments(Method method, Request req, Response res, Model model) {
        Parameter[] parameters = method.getParameters();
        return Arrays.stream(parameters)
                .map(param -> resolveParam(param, req, res,model))
                .toArray(Object[]::new);
    }
}
