package codesquad.framework.coffee.mvc;

import codesquad.framework.dispatcher.mv.Model;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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

    @Test
    void asMapTest(){
        //given
        String key1 = "key1";
        String value1 = "value1";

        String key2 = "key2";
        String value2 = "value2";
        model.addAttribute(key1,value1);
        model.addAttribute(key2,value2);

        //when
        Map<String, Object> map = model.asMap();

        //then
        assertTrue(map.containsKey(key1));
        assertTrue(map.containsKey(key2));
        assertEquals(value1,map.get(key1));
        assertEquals(value2,map.get(key2));
    }

    private class TestObject{

    }
}