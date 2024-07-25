package codesquad.framework;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ObjectMapper 클래스")
class ObjectMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("toJson 메서드는")
    class Describe_toJson {

        @Nested
        @DisplayName("기본 타입이 주어졌을 때")
        class Context_with_primitive_types {

            @Test
            @DisplayName("null을 변환한다")
            void it_converts_null() {
                assertEquals("null", objectMapper.toJson(null));
            }

            @Test
            @DisplayName("숫자를 변환한다")
            void it_converts_numbers() {
                assertEquals("42", objectMapper.toJson(42));
                assertEquals("3.14", objectMapper.toJson(3.14));
            }

            @Test
            @DisplayName("불리언을 변환한다")
            void it_converts_booleans() {
                assertEquals("true", objectMapper.toJson(true));
                assertEquals("false", objectMapper.toJson(false));
            }

            @Test
            @DisplayName("문자열을 변환한다")
            void it_converts_strings() {
                assertEquals("\"Hello, World!\"", objectMapper.toJson("Hello, World!"));
            }

            @Test
            @DisplayName("특수 문자가 포함된 문자열을 이스케이프 처리하여 변환한다")
            void it_escapes_special_characters() {
                assertEquals("\"\\\"\\\\\\b\\f\\n\\r\\t\"", objectMapper.toJson("\"\\\b\f\n\r\t"));
            }
        }

        @Nested
        @DisplayName("배열이 주어졌을 때")
        class Context_with_arrays {

            @Test
            @DisplayName("Object 배열을 변환한다")
            void it_converts_object_arrays() {
                Object[] array = {1, "two", true};
                assertEquals("[1,\"two\",true]", objectMapper.toJson(array));
            }

            @Test
            @DisplayName("기본 타입 배열을 변환한다")
            void it_converts_primitive_arrays() {
                int[] intArray = {1, 2, 3};
                assertEquals("[1,2,3]", objectMapper.toJson(intArray));
            }
        }

        @Nested
        @DisplayName("컬렉션이 주어졌을 때")
        class Context_with_collections {

            @Test
            @DisplayName("List를 변환한다")
            void it_converts_lists() {
                List<Object> list = Arrays.asList(1, "two", true);
                assertEquals("[1,\"two\",true]", objectMapper.toJson(list));
            }

            @Test
            @DisplayName("Set을 변환한다")
            void it_converts_sets() {
                Set<String> set = new LinkedHashSet<>(Arrays.asList("a", "b", "c"));
                assertEquals("[\"a\",\"b\",\"c\"]", objectMapper.toJson(set));
            }
        }

        @Nested
        @DisplayName("Map이 주어졌을 때")
        class Context_with_maps {

            @Test
            @DisplayName("Map을 변환한다")
            void it_converts_maps() {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("name", "John");
                map.put("age", 30);
                map.put("isStudent", false);
                assertEquals("{\"name\":\"John\",\"age\":30,\"isStudent\":false}", objectMapper.toJson(map));
            }
        }

        @Nested
        @DisplayName("커스텀 객체가 주어졌을 때")
        class Context_with_custom_objects {

            @Test
            @DisplayName("커스텀 객체를 변환한다")
            void it_converts_custom_objects() {
                TestObject obj = new TestObject("John", 30, true);
                assertEquals("{\"name\":\"John\",\"age\":30,\"isStudent\":true}", objectMapper.toJson(obj));
            }

            private static class TestObject {
                private String name;
                private int age;
                private boolean isStudent;

                TestObject(String name, int age, boolean isStudent) {
                    this.name = name;
                    this.age = age;
                    this.isStudent = isStudent;
                }
            }
        }
    }
}