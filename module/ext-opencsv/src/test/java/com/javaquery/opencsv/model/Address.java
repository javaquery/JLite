package com.javaquery.opencsv.model;

import com.javaquery.Exportable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author vicky.thakor
 * @since 2025-12-09
 */
@Getter
@Setter
@Builder
public class Address {
    @Exportable(key = "addressLine1")
    private String addressLine1;

    @Exportable(key = "addressLine2")
    private String addressLine2;

    @Exportable(key = "city")
    private String city;

    @Exportable(key = "state")
    private String state;

    @Exportable(key = "zipCode")
    private String zipCode;

    @Exportable(key = "country")
    private String country;
}
