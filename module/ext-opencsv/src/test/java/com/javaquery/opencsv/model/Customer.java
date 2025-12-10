package com.javaquery.opencsv.model;

import com.javaquery.Exportable;
import java.util.Set;
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
public class Customer {
    @Exportable(key = "firstName")
    private String firstName;

    @Exportable(key = "lastName")
    private String lastName;

    @Exportable(key = "email")
    private String email;

    @Exportable(key = "addresses")
    private Set<Address> addresses;
}
