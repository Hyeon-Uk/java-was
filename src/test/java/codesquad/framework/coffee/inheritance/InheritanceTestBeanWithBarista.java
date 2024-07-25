package codesquad.framework.coffee.inheritance;

import codesquad.framework.coffee.annotation.Barista;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Named;
import org.junit.jupiter.api.TestInfo;

@Coffee
public class InheritanceTestBeanWithBarista {
    private final TestInterface inheritanceA;
    private final TestInterface inheritanceB;

    @Barista
    public InheritanceTestBeanWithBarista(@Named("inA") TestInterface inheritanceA,
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
