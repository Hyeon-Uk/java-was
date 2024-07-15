//package codesquad.was.http.handler;
//
//import codesquad.application.handler.DefaultHandler;
//import codesquad.was.http.exception.HttpNotFoundException;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@Disabled
//class RequestHandlerMapperImplTest {
//    private RequestHandlerMapper mapper;
////            = new RequestHandlerMapperImpl();
//
//    @ParameterizedTest
//    @CsvSource(value={
//            "/,MainHandler",
//            "/user/create,RegisterHandler",
//            "/index.html,StaticResourceHandler"
//    })
//    void getRequestHandlerTest(String uri,String handlerClassName){
//        RequestHandler requestHandler = mapper.getRequestHandler(uri);
//        assertEquals(handlerClassName, requestHandler.getClass().getSimpleName());
//    }
//
//    @Test
//    void canNotFindAnyRequestHandlerTest(){
//        //given
//        String notExistsURI = "/NotExistHandlerURI";
//
//        //when & then
//        assertThrows(HttpNotFoundException.class, () -> mapper.getRequestHandler(notExistsURI));
//    }
//}