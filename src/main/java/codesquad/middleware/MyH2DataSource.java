package codesquad.middleware;

import codesquad.framework.coffee.annotation.Coffee;

@Coffee
public class MyH2DataSource implements DataSource{

    @Override
    public String getUrl() {
        return "jdbc:h2:mem:javawas;NON_KEYWORDS=USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
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
