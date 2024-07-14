package codesquad.framework.coffee;

import codesquad.framework.coffee.inheritance.*;
import codesquad.framework.coffee.test.TestBeanA;
import codesquad.framework.coffee.test.TestBeanB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
}