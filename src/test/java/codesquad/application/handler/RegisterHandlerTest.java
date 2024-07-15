//package codesquad.application.handler;
//
//import codesquad.middleware.UserDatabase;
//import codesquad.was.http.exception.HttpMethodNotAllowedException;
//import codesquad.was.http.message.request.HttpMethod;
//import codesquad.was.http.message.request.HttpRequest;
//import codesquad.was.http.message.response.HttpResponse;
//import codesquad.was.http.message.response.HttpStatus;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EnumSource;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RegisterHandlerTest {
//    private RegisterHandler registerHandler = new RegisterHandler(new UserDatabase());
//    private HttpRequest request;
//    private HttpResponse response;
//    private List<HttpMethod> excludeMethods = List.of(HttpMethod.POST);
//
//    @Test
//    void postRequestTest() {
//        //given
//        request = MockFactory.getHttpRequest(HttpMethod.POST, Map.of("userId","uk"),new HashMap<>(),"userId=uk&password=1234");
//        response = MockFactory.getHttpResponse();
//        //when
//        registerHandler.handle(request,response);
//
//        //then
//        assertEquals(HttpStatus.FOUND,response.getStatus());
//        assertTrue(response.getHeaders("Location").contains("/"));
//    }
//
//    @ParameterizedTest
//    @EnumSource(HttpMethod.class)
//    void unImplementsMethodTest(HttpMethod method){
//        if(!excludeMethods.contains(method)){
//            assertThrows(HttpMethodNotAllowedException.class,()->{
//                registerHandler.handle(MockFactory.getHttpRequest(method),
//                        MockFactory.getHttpResponse());
//            });
//        }
//    }
//}