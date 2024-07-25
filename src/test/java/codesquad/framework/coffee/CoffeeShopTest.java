package codesquad.framework.coffee;

import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.controllers.NotController;
import codesquad.framework.coffee.controllers.TestControllerA;
import codesquad.framework.coffee.controllers.TestControllerB;
import codesquad.framework.coffee.inheritance.*;
import codesquad.framework.coffee.multiplebeans.success.Daughter;
import codesquad.framework.coffee.multiplebeans.success.Parent;
import codesquad.framework.coffee.multiplebeans.success.Son;
import codesquad.framework.coffee.test.TestBeanA;
import codesquad.framework.coffee.test.TestBeanB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoffeeShopTest {
    CoffeeShop coffeeShop;
    @Nested
    @DisplayName("default test")
    class DefaultTest {
        @BeforeEach
        void setUp() throws Exception {
            coffeeShop = new CoffeeShop("codesquad.framework.coffee.test");
        }

        @Test
        void singletonTest() {
            TestBeanA bean1 = coffeeShop.getBean(TestBeanA.class);
            TestBeanA bean2 = coffeeShop.getBean(TestBeanA.class);

            assertEquals(bean2, bean1);
        }

        @Test
        void injectionTest() {
            TestBeanA bean = coffeeShop.getBean(TestBeanA.class);
            TestBeanB bean1 = coffeeShop.getBean(TestBeanB.class);
            assertEquals(bean, bean1.getTestBeanA());
        }

    }

    @Nested
    @DisplayName("inheritance injection test")
    class InheritanceTest {
        @BeforeEach
        void setUp() throws Exception {
            coffeeShop = new CoffeeShop("codesquad.framework.coffee.inheritance");
        }

        @Test
        void inheritanceInjectionTestWithBarista() {
            //given
            TestInterface inA = coffeeShop.getBean(InheritanceA.class);
            TestInterface inB = coffeeShop.getBean(InheritanceB.class);
            InheritanceTestBeanWithBarista bean = coffeeShop.getBean(InheritanceTestBeanWithBarista.class);

            //when&then
            assertEquals(inA,bean.getInheritanceA());
            assertEquals(inB,bean.getInheritanceB());
        }

        @Test
        void inheritanceInjectionTestWithoutBarista() {
            //given
            TestInterface inA = coffeeShop.getBean(InheritanceA.class);
            TestInterface inB = coffeeShop.getBean(InheritanceB.class);
            InheritanceTestBeanWithoutBarista bean = coffeeShop.getBean(InheritanceTestBeanWithoutBarista.class);

            //when&then
            assertEquals(inA,bean.getInheritanceA());
            assertEquals(inB,bean.getInheritanceB());
        }
    }

    @DisplayName("getAllBeansOfAnnotation")
    @Nested
    class GetAllBeansOfAnnotationTest {
        @BeforeEach
        void setUp() throws Exception {
            coffeeShop = new CoffeeShop("codesquad.framework.coffee.controllers");
        }
        @Test
        void getAllBeansOfAnnotationTest(){
            //given
            Class<? extends Annotation> annotation = Controller.class;
            TestControllerA expectedA = coffeeShop.getBean(TestControllerA.class);
            TestControllerB expectedB = coffeeShop.getBean(TestControllerB.class);
            NotController exclude = coffeeShop.getBean(NotController.class);

            //when
            List<?> result = coffeeShop.getAllBeansOfAnnotation(annotation);

            //then
            assertTrue(result.contains(expectedA));
            assertTrue(result.contains(expectedB));
            assertFalse(result.contains(exclude));
        }
    }

    @Nested
    @DisplayName("Multiple Beans Injection test")
    class MultipleBeansInjectionTest {
        private final String basePackage = "codesquad.framework.coffee.multiplebeans";
        @Test
        void failureOfMultipleBeansWithoutName() throws Exception {
            //given & when & then
            String message = assertThrows(Exception.class, () -> {
                coffeeShop = new CoffeeShop(basePackage + ".failure");
            }).getMessage();
            assertEquals("Multiple beans found of type "+basePackage+".failure.Child and no matching name found",message);
        }

        @Test
        void successOfMultipleBeansWithName() throws Exception {
            //given & when
            coffeeShop = new CoffeeShop(basePackage + ".success");
            Daughter daughter = coffeeShop.getBean(Daughter.class);
            Son son = coffeeShop.getBean(Son.class);
            Parent parent = coffeeShop.getBean(Parent.class);

            //then
            assertEquals(daughter,parent.getDaughter());
            assertEquals(son,parent.getSon());
        }
    }

    @Nested
    @DisplayName("More then two constructor test")
    class MoreThenTwoConstructorTest {
        private final String basePackage = "codesquad.framework.coffee.constructor";

        @Test
        void failureOfMoreThenTwoConstructorWithoutBarista() throws Exception {
            //given & when & then
            String message = assertThrows(Exception.class, () -> {
                coffeeShop = new CoffeeShop(basePackage + ".failure.without");
            }).getMessage();
            assertEquals("Multiple constructors without @Barista for "+basePackage+".failure.without.Parent",message);
        }

        @Test
        void failureOfMoreThenTwoConstructorWithMultipleBarista() throws Exception {
            //given & when & then
            String message = assertThrows(Exception.class, () -> {
                coffeeShop = new CoffeeShop(basePackage + ".failure.multiple");
            }).getMessage();
            assertEquals("Multiple @Barista constructors found for "+basePackage+".failure.multiple.Parent",message);
        }

        @Test
        void success() throws Exception {
            //given
            coffeeShop = new CoffeeShop(basePackage+".success");

            //when
            codesquad.framework.coffee.constructor.success.Parent parent = coffeeShop.getBean(codesquad.framework.coffee.constructor.success.Parent.class);
            codesquad.framework.coffee.constructor.success.Daughter daughter = coffeeShop.getBean(codesquad.framework.coffee.constructor.success.Daughter.class);
            codesquad.framework.coffee.constructor.success.Son son = coffeeShop.getBean(codesquad.framework.coffee.constructor.success.Son.class);

            //then
            assertEquals(daughter,parent.getDaughter());
            assertEquals(son,parent.getSon());
        }
    }

    @Nested
    @DisplayName("No bean found of type")
    class NoBeanFoundTest {
        private final String basePackage = "codesquad.framework.coffee.nobean";
        @Test
        void failureOfNoBeanFound() throws Exception {
            //given & when & then
            String message = assertThrows(Exception.class, () -> {
                coffeeShop = new CoffeeShop(basePackage);
            }).getMessage();
            assertEquals("No bean found of type "+basePackage+".NoBeanChild",message);
        }
    }
}