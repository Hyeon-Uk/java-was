package codesquad.was.http.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Session {
    private final static int DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS = 60*30;
    private final Map<String,Object> attributes = new HashMap<>();
    private long maxInactiveIntervalSeconds;
    private Date createdAt;
    private Date lastAccessedAt;
    private boolean expired;
    private String id;

    public Session(Date createdAt,Date lastAccessedAt){
        this(DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS,createdAt,lastAccessedAt);
    }

    public Session(long maxInactiveIntervalSeconds,Date createdAt,Date lastAccessedAt){
        setMaxInactiveIntervalSeconds(maxInactiveIntervalSeconds);
        setCreatedAt(createdAt);
        setLastAccessedAt(lastAccessedAt);
    }


    public long getMaxInactiveInterval() {
        return this.maxInactiveIntervalSeconds;
    }

    public void setMaxInactiveIntervalSeconds(long maxInactiveIntervalSeconds) {
        this.maxInactiveIntervalSeconds = maxInactiveIntervalSeconds;
    }

    public void set(String key, Object value) {
        attributes.put(key,value);
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(attributes.get(key));
    }

    public void remove(String key) {
        attributes.remove(key);
    }

    protected void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    protected void setLastAccessedAt(Date lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }


    public Date getCreatedAt() {
        return this.createdAt;
    }

    public Date getLastAccessedAt() {
        return this.lastAccessedAt;
    }

    public void invalidate(){
        this.expired = true;
    }

    protected void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }
}
