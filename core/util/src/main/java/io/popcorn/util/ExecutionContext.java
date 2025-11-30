package io.popcorn.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.popcorn.util.logging.Action;
import io.popcorn.util.logging.ActivityStatus;
import io.popcorn.util.time.Dates;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class ExecutionContext<T, V> {

    private String requestId;
    private T referenceId;
    @JsonProperty("user")
    private V userContext;

    private Action action;
    private ActivityStatus activityStatus;

    private Map<String, Object> meta;
    private Integer maxRetries = 5;
    private Integer retriesAttempted = 0;
    private final Date createdAt;

    public ExecutionContext() {
        this.createdAt = Dates.current();
        this.meta = new HashMap<>();
    }

    public ExecutionContext(String requestId){
        this.requestId = requestId;
        this.meta = new HashMap<>();
        this.createdAt = Dates.current();
    }

    public ExecutionContext(String requestId, T referenceId, Action action) {
        this.requestId = requestId;
        this.referenceId = referenceId;
        this.action = action;
        this.meta = new HashMap<>();
        this.createdAt = Dates.current();
    }

    public ExecutionContext(T referenceId, Action action) {
        this(UniqueIdGenerator.generate(), referenceId, action);
    }

    public ExecutionContext(T referenceId, Action action, Integer maxRetries) {
        this(referenceId, action);
        this.maxRetries = maxRetries;
    }

    public ExecutionContext(Action action, Integer maxRetries) {
        this(null, action, maxRetries);
    }

    public ExecutionContext(Action action) {
        this(action, 5);
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public V getUserContext() {
        return userContext;
    }

    public void setUserContext(V userContext) {
        this.userContext = userContext;
    }

    public T getReferenceId() {
        return referenceId;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(ActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public Object getMeta(String key, Object defaultValue){
        return meta.getOrDefault(key, defaultValue);
    }

    public String optString(String key, String defaultValue){
        return String.valueOf(getMeta(key, defaultValue));
    }

    public void addMeta(String key, Object value){
        this.meta.put(key, value);
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public Integer getRetriesAttempted() {
        return retriesAttempted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void addRetriesAttempted(Integer retriesAttempted) {
        this.retriesAttempted += retriesAttempted;
    }

    @Override
    public String toString() {
        return "ExecutionContext{" +
                "requestId='" + requestId + '\'' +
                ", referenceId=" + referenceId +
                ", action=" + action +
                ", maxRetries=" + maxRetries +
                ", retriesAttempted=" + retriesAttempted +
                ", createdAt=" + createdAt +
                '}';
    }
}
