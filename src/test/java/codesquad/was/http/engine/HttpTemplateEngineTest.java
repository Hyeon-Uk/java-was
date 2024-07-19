package codesquad.was.http.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("HttpTemplateEngine 클래스")
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

    @Nested
    @DisplayName("render 메소드는")
    class RenderMethod {

        @Test
        @DisplayName("값 치환을 올바르게 수행한다")
        public void testRenderWithValueReplacement() throws IllegalAccessException {
            // given
            String template = "Hello {{name}}, you are {{age}} years old.";

            // when
            String result = engine.render(template, context);

            // then
            assertEquals("Hello John Doe, you are 30 years old.", result);
        }

        @Test
        @DisplayName("for 루프를 올바르게 처리한다")
        public void testRenderWithForLoop() throws IllegalAccessException {
            // given
            String template = "{{for item in {{items}} Item: (item)}}";

            // when
            String result = engine.render(template, context);

            // then
            assertEquals("Item: item1Item: item2Item: item3", result.trim());
        }

        @Test
        @DisplayName("for 루프와 멤버 변수를 올바르게 처리한다")
        public void testRenderWithForLoopAndMemberVariable() throws IllegalAccessException {
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

        @Nested
        @DisplayName("if-else 조건문을")
        class IfElseCondition {

            @Test
            @DisplayName("조건이 참일 때 올바르게 처리한다")
            public void testRenderWithIfElseConditionTrue() throws IllegalAccessException {
                // given
                String template = "{{if {{age}} == 30 then You are 30 years old else You are not 30 years old}}";

                // when
                String result = engine.render(template, context);

                // then
                assertEquals("You are 30 years old", result);
            }

            @Test
            @DisplayName("조건이 거짓일 때 올바르게 처리한다")
            public void testRenderWithIfElseConditionFalse() throws IllegalAccessException {
                // given
                String template = "{{if {{age}} != 30 then You are 30 years old else You are not 30 years old}}";

                // when
                String result = engine.render(template, context);

                // then
                assertEquals("You are not 30 years old", result);
            }
        }

        @Test
        @DisplayName("null 값을 올바르게 처리한다")
        public void testRenderWithNullValue() throws IllegalAccessException {
            // given
            String template = "Null value: {{nullValue}}";

            // when
            String result = engine.render(template, context);

            // then
            assertEquals("Null value: null", result);
        }
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