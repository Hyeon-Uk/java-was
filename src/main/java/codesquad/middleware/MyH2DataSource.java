package codesquad.middleware;

import codesquad.framework.coffee.annotation.Coffee;

@Coffee
public class MyH2DataSource implements DataSource{

    @Override
    public String getUrl() {
        return "jdbc:h2:mem:javawas";
    }

    @Override
    public String getUsername() {
        return "sa";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }
}
