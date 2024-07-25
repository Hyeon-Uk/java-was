package codesquad.was.http.cookie;

import codesquad.was.http.message.InvalidRequestFormatException;

public class Cookie {
    private String name;
    private String value;
    private String path;
    private String domain;
    private long maxAge;

    public Cookie(String name, String value) {
        if (name == null) throw new InvalidRequestFormatException();

        this.name = name.trim();
        setValue(value);

        //default setting
        this.path = "/";
        this.domain = null;
        this.maxAge = -1;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = (value == null ? "" : value.trim());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
}
