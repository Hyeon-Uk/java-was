package codesquad.was.http.engine;

import codesquad.was.http.engine.HttpTemplateEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTemplateEngineTest {

    private Map<String, Object> context;
    private HttpTemplateEngine engine = new HttpTemplateEngine();

    @BeforeEach
    public void setUp() {
        context = new HashMap<>();
        List<String> items = Arrays.asList("item1", "item2", "item3");
        context.put("items", items);
        context.put("name", "John Doe");
        context.put("age", 30);
        context.put("nullValue", null);
    }

    @Test
    public void testRenderWithValueReplacement() throws IllegalAccessException {
        // given
        String template = "Hello {{name}}, you are {{age}} years old.";

        // when
        String result = engine.render(template, context);

        // then
        assertEquals("Hello John Doe, you are 30 years old.", result);
    }

    @Test
    public void testRenderWithForLoop() throws IllegalAccessException {
        // given
        String template = "{{for item in {{items}} Item: (item)}}";

        // when
        String result = engine.render(template, context);

        // then
        assertEquals("Item: item1Item: item2Item: item3", result.trim());
    }

    @Test
    public void testRenderWithForLoopAndMemberVariable() throws IllegalAccessException{
        // given
        List<User> users = List.of(new User("name1",1),new User("name2",2),new User("name3",3));
        Map<String,Object> ctx = new HashMap<>();
        ctx.put("users",users);
        String template = "{{for user in {{users}} user:{(user.name),(user.age)} }}";

        // when
        String result = engine.render(template, ctx);

        // then
        assertEquals("user:{name1,1} user:{name2,2} user:{name3,3}", result.trim());
    }

    @Test
    public void testRenderWithIfElseConditionTrue() throws IllegalAccessException {
        // given
        String template = "{{if {{age}} == 30 then You are 30 years old else You are not 30 years old}}";

        // when
        String result = engine.render(template, context);

        // then
        assertEquals("You are 30 years old", result);
    }

    @Test
    public void testRenderWithIfElseConditionFalse() throws IllegalAccessException {
        // given
        String template = "{{if {{age}} != 30 then You are 30 years old else You are not 30 years old}}";

        // when
        String result = engine.render(template, context);

        // then
        assertEquals("You are not 30 years old", result);
    }

    @Test
    public void testRenderWithNullValue() throws IllegalAccessException {
        // given
        String template = "Null value: {{nullValue}}";

        // when
        String result = engine.render(template, context);

        // then
        assertEquals("Null value: null", result);
    }

    static class User {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
