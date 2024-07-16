package codesquad.framework.coffee.mvc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    private Model model = new Model();
    @Test
    void addAndGetString(){
        //given
        String key = "hello world";
        String value = "world";

        //when
        model.addAttribute(key,value);
        Object result = model.getAttribute(key);

        //then
        assertEquals(value,result);
    }

    @Test
    void addAndGetOther(){
        //given
        String key = "key";
        TestObject value = new TestObject();

        //when
        model.addAttribute(key,value);
        Object result = model.getAttribute(key);

        //then
        assertEquals(value,result);
    }

    @Test
    void collectionTest(){
        //given
        List<String> collection = List.of("hello","world");
        String key = "key";

        //when
        model.addAttribute(key,collection);
        List<String> result = (List<String>)model.getAttribute(key);

        //then
        assertTrue(result.contains("hello"));
        assertTrue(result.contains("world"));
    }

    @Test
    void notExistsKey(){
        //given
        String key = "key";
        TestObject value = new TestObject();
        model.addAttribute(key,value);

        //when
        Object result = model.getAttribute("notExistKey");

        //then
        assertNull(result);
    }

    private class TestObject{

    }
}