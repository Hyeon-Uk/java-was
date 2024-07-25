package codesquad.framework.coffee.multiplebeans.failure;

import codesquad.framework.coffee.annotation.Coffee;

@Coffee
public class Parent {
    private Child child1;
    private Child child2;
    public Parent(Child child1,Child child2){
        this.child1 = child1;
        this.child2 = child2;
    }
}
