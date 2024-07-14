package codesquad.was.http.message.request;

public enum HttpMethod {
    GET("GET"),POST("POST"),PUT("PUT"),DELETE("DELETE"),PATCH("PATCH"),HEAD("HEAD"),OPTIONS("OPTIONS"),TRACE("TRACE"),CONNECT("CONNECT");
    private final String method;
    HttpMethod(String method) {
        this.method = method;
    }
    public String getMethod(){
        return this.method;
    }
    public static HttpMethod from(String method){
        if(method == null || method.isEmpty()) return null;
        String upperMethod = method.toUpperCase();
        for(HttpMethod httpMethod : HttpMethod.values()){
            if(httpMethod.getMethod().equals(upperMethod)){
                return httpMethod;
            }
        }
        return null;
    }
}
