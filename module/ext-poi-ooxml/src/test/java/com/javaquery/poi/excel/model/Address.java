package com.javaquery.poi.excel.model;

import com.javaquery.annotations.Exportable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;

/**
 * @author vicky.thakor
 * @since 2025-12-23
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

    public static List<Address> fakeData(int count) {
        Faker faker = new Faker();
        return faker.collection()
                .len(count)
                .suppliers(() -> createAddress(faker))
                .build()
                .get();
    }

    private static Address createAddress(Faker faker) {
        return Address.builder()
                .addressLine1(faker.address().streetAddress())
                .addressLine2(faker.address().secondaryAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .country(faker.address().country())
                .build();
    }
}
