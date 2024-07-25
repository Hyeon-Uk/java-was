//package codesquad.application.handler;
//
//import codesquad.was.http.exception.HttpMethodNotAllowedException;
//import codesquad.was.http.message.request.HttpMethod;
//import codesquad.was.http.message.request.HttpRequest;
//import codesquad.was.http.message.response.HttpResponse;
//import codesquad.was.http.message.response.HttpStatus;
//import codesquad.was.utils.FileUtils;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EnumSource;
//
//import java.util.HashMap;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class MainHandlerTest {
//    private MainHandler mainHandler = new MainHandler(new FileUtils());
//    private HttpRequest request;
//    private HttpResponse response;
//    private List<HttpMethod> excludeMethods = List.of(HttpMethod.GET);
//
//    @Test
//    void getRequestTest() {
//        //given
//        request = MockFactory.getHttpRequest(HttpMethod.GET,new HashMap<>(),new HashMap<>(),"");
//        response = MockFactory.getHttpResponse();
//
//        //when
//        mainHandler.getHandle(request,response);
//
//        //then
//        assertEquals(HttpStatus.OK,response.getStatus());
//    }
//
//    @ParameterizedTest
//    @EnumSource(HttpMethod.class)
//    void unImplementsMethodTest(HttpMethod method){
//        if(!excludeMethods.contains(method)){
//            assertThrows(HttpMethodNotAllowedException.class,()->{
//                mainHandler.handle(MockFactory.getHttpRequest(method),
//                        MockFactory.getHttpResponse());
//            });
//        }
//    }
//}