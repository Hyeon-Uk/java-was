package codesquad.framework.coffee.inheritance;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Named;

@Coffee
public class InheritanceTestBeanWithoutBarista {
    private final TestInterface inheritanceA;
    private final TestInterface inheritanceB;

    public InheritanceTestBeanWithoutBarista(@Named("inA") TestInterface inheritanceA,
                                             @Named("inB") TestInterface inheritanceB) {
        this.inheritanceA = inheritanceA;
        this.inheritanceB = inheritanceB;
    }

    public TestInterface getInheritanceA() {
        return inheritanceA;
    }

    public TestInterface getInheritanceB() {
        return inheritanceB;
    }
}
