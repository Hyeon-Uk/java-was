package codesquad.middleware;

public interface DataSource {
    String getUrl();
    String getUsername();
    String getPassword();
    String getDriverClassName();
}
