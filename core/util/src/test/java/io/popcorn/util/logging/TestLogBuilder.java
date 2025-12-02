package io.popcorn.util.logging;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
public class TestLogBuilder {

    public enum UtilLogAction implements Action {
        testAction;
    }

    @Test
    public void test_common() {
        LogBuilder logBuilder = new LogBuilder(UtilLogAction.testAction);
        logBuilder.setExecutionStartTime();
        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logBuilder.buildMessage("Hello");
        logBuilder.buildMessage("World!");
        logBuilder.buildMessage("IntKey", 0);
        logBuilder.addTag(TestLogBuilder.class.getSimpleName());
        logBuilder.setExecutionEndTime();

        Map<String, Object> attributes = logBuilder.getAttributes();
        List<String> tags = (List<String>) logBuilder.get(LogBuilder.TAGS);
        Assertions.assertEquals(TestLogBuilder.class.getSimpleName(), tags.get(0));
        Assertions.assertEquals("Hello\nWorld!\nIntKey : 0", logBuilder.getMessage());
        Assertions.assertTrue(attributes.size() > 0);
    }
}
