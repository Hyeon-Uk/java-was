package codesquad.framework.resolver.tc;

import codesquad.framework.dispatcher.mv.Model;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.session.Session;

public class TestObject {
    private String value;
    private int intValue;
    private Integer IntegerValue;
    private long longValue;
    private Long LongValue;
    private float floatValue;
    private Float FloatValue;
    private double doubleValue;
    private Double DoubleValue;
    private Model model;
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private Session session;
    public TestObject() {}

    public Float getFFloatValue(){
        return FloatValue;
    }
    public Long getLLongValue(){
        return LongValue;
    }
    public Double getDDoubleValue(){
        return DoubleValue;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return intValue;
    }

    public Integer getIntegerValue() {
        return IntegerValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public Model getModel() {
        return model;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public Session getSession() {
        return session;
    }
}
