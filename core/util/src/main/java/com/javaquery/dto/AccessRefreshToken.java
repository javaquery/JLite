package com.javaquery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author vicky.thakor
 * @since 1.2.8
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class AccessRefreshToken {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
}
