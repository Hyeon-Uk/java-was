package codesquad.framework.coffee.multiplebeans.success;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Named;

@Coffee
public class Parent {
    private Child daughter;
    private Child son;
    public Parent(@Named("daughter") Child child1,
                  @Named("son") Child child2){
        this.daughter = child1;
        this.son = child2;
    }

    public Child getDaughter() {
        return daughter;
    }

    public Child getSon() {
        return son;
    }
}
