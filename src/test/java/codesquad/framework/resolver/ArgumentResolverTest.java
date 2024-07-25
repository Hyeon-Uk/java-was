package codesquad.framework.resolver;

import codesquad.application.handler.MockFactory;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.framework.resolver.tc.TestController;
import codesquad.framework.resolver.tc.TestObject;
import codesquad.message.mock.MockTimer;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.session.Session;
import codesquad.was.http.session.SessionManager;
import codesquad.was.http.session.SessionStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentResolverTest {
    private ArgumentResolver argumentResolver;
    private Map<String,String> requestQueryString;
    private Map<String, List<String>> requestHeader;
    private HttpRequest mockRequest;
    private HttpResponse mockResponse;
    private Model mockModel;

    @BeforeEach
    public void setUp() {
        requestQueryString = new HashMap<>();
        requestHeader = new HashMap<>();
        argumentResolver = new ArgumentResolver();
        mockRequest = new HttpRequest(new HttpRequestStartLine("version","/",HttpMethod.GET,requestQueryString),
                requestQueryString,new HttpHeader(requestHeader),new HttpBody(),new SessionManager(new SessionStorage(),new MockTimer(1l)));
        mockResponse = MockFactory.getHttpResponse();
        mockModel = new Model();
    }

    @Test
    public void testResolveArguments_StringParam() throws Exception {
        Method method = TestController.class.getMethod("stringParamMethod", String.class);
        requestQueryString.put("param","test");
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        assertEquals("test", resolvedArgs[0]);
    }

    @Test
    public void testResolveArguments_IntegerParam() throws Exception {
        Method method = TestController.class.getMethod("intParamMethod", int.class,Integer.class);
        requestQueryString.put("param","123");
        requestQueryString.put("param2","321");
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(2, resolvedArgs.length);
        assertEquals(123, resolvedArgs[0]);
        assertEquals(321, resolvedArgs[1]);
    }

    @Test
    public void testResolveArguments_LongParam() throws Exception {
        Method method = TestController.class.getMethod("longParamMethod", long.class,Long.class);
        requestQueryString.put("param","123");
        requestQueryString.put("param2","321");
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(2, resolvedArgs.length);
        assertEquals(123, (long)resolvedArgs[0]);
        assertEquals(321, (Long)resolvedArgs[1]);
    }

    @Test
    public void testResolveArguments_floatParam() throws Exception {
        Method method = TestController.class.getMethod("floatParamMethod", float.class,Float.class);
        requestQueryString.put("param","123");
        requestQueryString.put("param2","321");
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(2, resolvedArgs.length);
        assertEquals(123f, (float)resolvedArgs[0],0.1f);
        assertEquals(321f, (Float)resolvedArgs[1],0.1f);
    }

    @Test
    public void testResolveArguments_doubleParam() throws Exception {
        Method method = TestController.class.getMethod("doubleParamMethod", double.class,Double.class);
        requestQueryString.put("param","123");
        requestQueryString.put("param2","321");
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(2, resolvedArgs.length);
        assertEquals(123.0, (double)resolvedArgs[0],0.1f);
        assertEquals(321.0, (Double)resolvedArgs[1],0.1f);
    }

    @Test
    public void testResolveArguments_ObjectParam() throws Exception {
        Method method = TestController.class.getMethod("objectParamMethod", TestObject.class);
        requestQueryString.put("value","testValue");
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        TestObject testObject = (TestObject) resolvedArgs[0];
        assertNotNull(testObject);
        assertEquals("testValue", testObject.getValue());
    }

    @Test
    public void testResolveArguments_SessionParamWithTrue() throws Exception {
        Method method = TestController.class.getMethod("sessionParamMethod", Session.class);
        mockRequest.getHeader("Cookie").add("SID=session_id");
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        Session session = (Session) resolvedArgs[0];
        assertNotNull(session);
        assertNotNull(session.getId());
    }

    @Test
    public void testResolveArguments_SessionParamWithFalse() throws Exception {
        Method method = TestController.class.getMethod("sessionParamMethodWithFalse", Session.class);
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        Session session = (Session) resolvedArgs[0];
        assertNull(session);
    }

    @Test
    public void testResolveArguments_SessionParamWithoutAnnotation() throws Exception {
        Method method = TestController.class.getMethod("sessionParamMethodWithoutAnnotation", Session.class);
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        Session session = (Session) resolvedArgs[0];
        assertNull(session);
    }


    @Test
    public void testResolveArguments_HttpRequestMethod() throws Exception {
        Method method = TestController.class.getMethod("httpRequestMethod", HttpRequest.class);
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        assertEquals(mockRequest, resolvedArgs[0]);
    }

    @Test
    public void testResolveArguments_HttpResponseMethod() throws Exception {
        Method method = TestController.class.getMethod("httpResponseMethod", HttpResponse.class);
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        assertEquals(mockResponse, resolvedArgs[0]);
    }

    @Test
    public void testResolveArguments_ModelMethod() throws Exception {
        Method method = TestController.class.getMethod("modelMethod", Model.class);
        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        assertNotNull(resolvedArgs[0]);
        assertEquals(mockModel,resolvedArgs[0]);
    }

    @Test
    public void testResolveArguments_TestObjectMethod() throws Exception {
        Method method = TestController.class.getMethod("testObjectMethod", TestObject.class);
        requestQueryString.put("intValue","1");
        requestQueryString.put("IntegerValue","2");
        requestQueryString.put("longValue","3");
        requestQueryString.put("LongValue","4");
        requestQueryString.put("floatValue","5");
        requestQueryString.put("FloatValue","6");
        requestQueryString.put("doubleValue","7");
        requestQueryString.put("DoubleValue","8");

        Object[] resolvedArgs = argumentResolver.resolveArguments(method, mockRequest, mockResponse, mockModel);
        assertNotNull(resolvedArgs);
        assertEquals(1, resolvedArgs.length);
        TestObject testObject = (TestObject) resolvedArgs[0];
        assertEquals(1, testObject.getIntValue());
        assertEquals(2, testObject.getIntegerValue());
        assertEquals(3, testObject.getLongValue());
        assertEquals(4, testObject.getLLongValue());
        assertEquals(5f, testObject.getFloatValue(),0.1);
        assertEquals(6f, testObject.getFFloatValue(),0.1);
        assertEquals(7, testObject.getDoubleValue(),0.1);
        assertEquals(8,testObject.getDDoubleValue(),0.1);
        assertEquals(mockRequest,testObject.getHttpRequest());
        assertEquals(mockResponse,testObject.getHttpResponse());
        assertEquals(mockModel,testObject.getModel());
    }
}