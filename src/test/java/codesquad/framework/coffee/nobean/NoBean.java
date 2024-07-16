package codesquad.framework.coffee.nobean;

import codesquad.framework.coffee.annotation.Coffee;

@Coffee
public class NoBean {
    private NoBeanChild child;
    public NoBean(NoBeanChild child){
        this.child = child;
    }
}
