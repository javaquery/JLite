package io.popcorn.util.logging;

import java.util.*;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public final class LogBuilder {

    public static final String ACTION = "action";
    public static final String MESSAGE = "message";
    public static final String TAGS = "tags";
    public static final String EXECUTION_TIME = "executionTime";

    private final Map<String, Object> attributes;
    private final StringJoiner messageBuilder;
    private String message;
    private final List<String> tags;
    private long executionStartTime;

    public LogBuilder(Action action) {
        attributes = new HashMap<>();
        tags = new ArrayList<>();
        messageBuilder = new StringJoiner("\n");
        attributes.put(ACTION, action);
    }

    /**
     * Gets all attributes to log.
     *
     * @return the attributes
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Put key-value in logger
     *
     * @param key   the key
     * @param value the value can be any object
     * @return the log builder
     */
    public LogBuilder put(String key, Object value){
        attributes.put(key, value);
        return this;
    }

    /**
     * Get object from attributes
     *
     * @param key the key
     * @return the object
     */
    public Object get(String key){
        return attributes.get(key);
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
        put(MESSAGE, this.message);
    }

    /**
     * Build/Append message when you have step by step execution and final message will
     * be one single string.
     *
     * @param message the message
     */
    public void buildMessage(Object message){
        if(Objects.nonNull(message)){
            messageBuilder.add(message.toString());
            setMessage(messageBuilder.toString());
        }
    }

    /**
     * Build/Append message using key-value.
     *
     * @param key   the key
     * @param value the value
     */
    public void buildMessage(Object key, Object value){
        if(Objects.nonNull(key) && Objects.nonNull(value)){
            buildMessage(String.format("%s : %s", key, value));
        }
    }

    /**
     * Sets execution start time.
     */
    public void setExecutionStartTime() {
        executionStartTime = System.currentTimeMillis();
    }

    /**
     * Set execution end time and executionTime (milliseconds) will be added in attributes.
     */
    public void setExecutionEndTime(){
        if(executionStartTime != 0){
            attributes.put(EXECUTION_TIME, (System.currentTimeMillis() - executionStartTime));
        }
    }

    /**
     * Add tag to you log
     *
     * @param tag the tag
     */
    public void addTag(String tag){
        tags.add(tag);
        put(TAGS, tags);
    }
}
