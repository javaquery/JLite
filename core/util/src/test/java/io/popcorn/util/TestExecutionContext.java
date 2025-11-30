package io.popcorn.util;

import io.popcorn.util.logging.Action;
import io.popcorn.util.logging.ActivityStatus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author vicky.thakor
 * @since 1.2.0
 */
public class TestExecutionContext {

    public enum ExecutionContextAction implements Action {
        ONE
    }

    public class UserContext{

        private Long userId;

        private Long tenantId;

        public UserContext(Long userId, Long tenantId) {
            this.userId = userId;
            this.tenantId = tenantId;
        }

        public Long getUserId() {
            return userId;
        }

        public Long getTenantId() {
            return tenantId;
        }
    }

    @Test
    public void defaultConstructor(){
        ExecutionContext<Object, Object> executionContext = new ExecutionContext<>();
        executionContext.setUserContext(new UserContext(50L, 20L));
        executionContext.setRequestId(UniqueIdGenerator.generate());

        UserContext userContext = (UserContext) executionContext.getUserContext();
        assertNotNull(executionContext.getMeta());
        assertEquals(50L, userContext.getUserId());
        assertNotNull(executionContext.getCreatedAt());
        assertNotNull(executionContext.getRequestId());
    }

    @Test
    public void constructorWithRequestId(){
        ExecutionContext<Long, Long> executionContext = new ExecutionContext<>(UniqueIdGenerator.generate());
        assertNotNull(executionContext.getRequestId());
        assertNotNull(executionContext.getMeta());
        assertNotNull(executionContext.getCreatedAt());
    }

    @Test
    public void constructorWithRequestIdReferenceIdAction(){
        ExecutionContext<Long, Long> executionContext = new ExecutionContext<>(UniqueIdGenerator.generate(), 1L, ExecutionContextAction.ONE);
        executionContext.setUserContext(50L);
        executionContext.setActivityStatus(ActivityStatus.STARTED);
        assertNotNull(executionContext.getRequestId());
        assertEquals(ExecutionContextAction.ONE, executionContext.getAction());
        assertEquals(1L, executionContext.getReferenceId());
        assertNotNull(executionContext.getMeta());
        assertNotNull(executionContext.getCreatedAt());

        assertEquals(50L, executionContext.getUserContext());
        assertEquals(ActivityStatus.STARTED, executionContext.getActivityStatus());
        assertNotNull(executionContext.getCreatedAt());
    }

    @Test
    public void constructorWithReferenceIdAction(){
        ExecutionContext<String, Void> executionContext = new ExecutionContext<>("test", ExecutionContextAction.ONE);
        assertNotNull(executionContext.getRequestId());
        assertEquals(ExecutionContextAction.ONE, executionContext.getAction());
        assertEquals("test", executionContext.getReferenceId());
        assertNotNull(executionContext.getMeta());
        assertNotNull(executionContext.getCreatedAt());
    }

    @Test
    public void constructorWithAction(){
        ExecutionContext<String, Void> executionContext = new ExecutionContext<>(ExecutionContextAction.ONE);
        assertNotNull(executionContext.getRequestId());
        assertEquals(ExecutionContextAction.ONE, executionContext.getAction());
        assertNull(executionContext.getReferenceId());
        assertNotNull(executionContext.getMeta());
        assertNotNull(executionContext.getCreatedAt());
    }

    @Test
    public void constructorWithActionAndMeta(){
        ExecutionContext<String, Void> executionContext = new ExecutionContext<>(ExecutionContextAction.ONE);
        executionContext.addMeta("key", "value");
        assertNotNull(executionContext.getRequestId());
        assertEquals(ExecutionContextAction.ONE, executionContext.getAction());
        assertNull(executionContext.getReferenceId());
        assertEquals("value", executionContext.getMeta("key", null));
        assertEquals("not found", executionContext.optString("key2", "not found"));
        assertNotNull(executionContext.getCreatedAt());

        /* set meta */
        executionContext.setMeta(new HashMap<>());
        assertNull(executionContext.getMeta("key", null));
    }

    @Test
    public void constructorWithActionAndRetriesAttempted(){
        ExecutionContext<String, Void> executionContext = new ExecutionContext<>(ExecutionContextAction.ONE);
        executionContext.addRetriesAttempted(1);
        assertNotNull(executionContext.getRequestId());
        assertEquals(ExecutionContextAction.ONE, executionContext.getAction());
        assertNull(executionContext.getReferenceId());
        assertNotNull(executionContext.getMeta());
        assertEquals(1, executionContext.getRetriesAttempted());
        assertNotNull(executionContext.getCreatedAt());
    }

    @Test
    public void constructorWithReferenceIdActionMaxRetries(){
        ExecutionContext<Long, Void> executionContext = new ExecutionContext<>(1L, ExecutionContextAction.ONE, 3);
        assertNotNull(executionContext.getRequestId());
        assertEquals(ExecutionContextAction.ONE, executionContext.getAction());
        assertEquals(1L, executionContext.getReferenceId());
        assertEquals(3, executionContext.getMaxRetries());
        assertNotNull(executionContext.getMeta());
        assertNotNull(executionContext.getCreatedAt());
    }

    @Test
    public void constructorWithActionMaxRetries(){
        ExecutionContext<Long, Void> executionContext = new ExecutionContext<>(ExecutionContextAction.ONE, 3);
        assertNotNull(executionContext.getRequestId());
        assertEquals(ExecutionContextAction.ONE, executionContext.getAction());
        assertNull(executionContext.getReferenceId());
        assertEquals(3, executionContext.getMaxRetries());
        assertNotNull(executionContext.getMeta());
        assertNotNull(executionContext.getCreatedAt());
    }

    @Test
    public void metaDataTest(){
        ExecutionContext<String, Void> executionContext = new ExecutionContext<>();
        executionContext.addMeta("key", "value");
        assertEquals("value", executionContext.getMeta("key", null));
        assertEquals("value", executionContext.optString("key", null));
        assertEquals("not found", executionContext.optString("key2", "not found"));
        executionContext.addMeta("key", "value2");
        assertEquals("value2", executionContext.getMeta("key", null));
    }
}
