package com.javaquery.spring.mx;

import java.io.File;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
public class EMailPayload {
    private String from;
    private String fromName;
    private String replyTo;
    private Iterable<String> to;
    private Iterable<String> cc;
    private Iterable<String> bcc;
    private String subject;
    private String body;
    private boolean isHtml;
    private Iterable<File> attachments;
}
