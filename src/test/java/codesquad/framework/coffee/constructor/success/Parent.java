package codesquad.framework.coffee.constructor.success;

import codesquad.framework.coffee.annotation.Barista;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Named;

@Coffee
public class Parent {
    private Child daughter;
    private Child son;

    @Barista
    public Parent(@Named("daughter") Child child1,
                  @Named("son") Child child2){
        this.daughter = child1;
        this.son = child2;
    }

    public Parent(){}

    public Child getDaughter() {
        return daughter;
    }

    public Child getSon() {
        return son;
    }
}
