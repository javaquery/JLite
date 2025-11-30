package io.popcorn.util.http;

import io.popcorn.util.collection.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 1.2.0
 */
public class TestCommonResponse {

    @Test
    public void ok(){
        CommonResponse<String> commonResponse = CommonResponse.ok("payload");
        Assertions.assertEquals("payload", commonResponse.getPayload());
    }

    @Test
    public void ofWithStatusCodeMessagePayload(){
        CommonResponse<String> commonResponse = CommonResponse.of(HttpStatusCode.CREATED, "message", "payload");
        Assertions.assertEquals(HttpStatusCode.CREATED.value(), commonResponse.getStatusCode());
        Assertions.assertEquals("message", commonResponse.getMessage());
        Assertions.assertEquals("payload", commonResponse.getPayload());
    }

    @Test
    public void ofWithStatusCodeMessage(){
        CommonResponse<String> commonResponse = CommonResponse.of(HttpStatusCode.CREATED, "message");
        Assertions.assertEquals(HttpStatusCode.CREATED.value(), commonResponse.getStatusCode());
        Assertions.assertEquals("message", commonResponse.getMessage());
    }

    @Test
    public void ofWithStatusCodePayload(){
        CommonResponse<Long> commonResponse = CommonResponse.of(HttpStatusCode.CREATED, 1L);
        Assertions.assertEquals(HttpStatusCode.CREATED.value(), commonResponse.getStatusCode());
        Assertions.assertEquals(1L, commonResponse.getPayload());
    }

    @Test
    public void ofWithStatusCodeErrorMessages(){
        CommonResponse<Long> commonResponse = CommonResponse.of(HttpStatusCode.BAD_REQUEST, Collections.singletonList("errorMessage"));
        Assertions.assertEquals(HttpStatusCode.BAD_REQUEST.value(), commonResponse.getStatusCode());
        Assertions.assertEquals("errorMessage", commonResponse.getErrorMessages().get(0));
    }

    @Test
    public void okWithPaging(){
        CommonResponse<Long> commonResponse = CommonResponse.of(HttpStatusCode.CREATED, 1L);
        commonResponse.withPage(1).withLimit(10).withTotal(100L);
        Assertions.assertEquals(HttpStatusCode.CREATED.value(), commonResponse.getStatusCode());
        Assertions.assertEquals(1L, commonResponse.getPayload());
        Assertions.assertEquals(1, commonResponse.getPage());
        Assertions.assertEquals(10, commonResponse.getLimit());
        Assertions.assertEquals(100L, commonResponse.getTotal());
    }
}
