package com.javaquery.opencsv.model;

import com.javaquery.Exportable;
import lombok.*;
import net.datafaker.Faker;

/**
 * @author vicky.thakor
 * @since 2025-12-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passport {
    @Exportable(key = "passportNumber")
    private String passportNumber;

    @Exportable(key = "country")
    private String country;

    @Exportable(key = "expirationDate")
    private String expirationDate;

    public static Passport fakeData() {
        Faker faker = new Faker();
        return Passport.builder()
                .passportNumber(faker.idNumber().valid())
                .country(faker.country().name())
                .expirationDate(faker.timeAndDate()
                        .future(3650, java.util.concurrent.TimeUnit.DAYS)
                        .toString())
                .build();
    }
}
