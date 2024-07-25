package codesquad.framework.coffee.test;

import codesquad.framework.coffee.annotation.Coffee;

@Coffee
public class TestBeanB {
    private TestBeanA testBeanA;

    public TestBeanB(TestBeanA testBeanA) {
        this.testBeanA = testBeanA;
    }

    public TestBeanA getTestBeanA(){
        return testBeanA;
    }
}
