package codesquad.framework.coffee.mvc;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private Map<String,Object> model;
    public Model(){
        model = new HashMap<>();
    }

    public void addAttribute(String name,Object value){
        model.put(name,value);
    }

    public Object getAttribute(String name){
        return model.get(name);
    }
}
