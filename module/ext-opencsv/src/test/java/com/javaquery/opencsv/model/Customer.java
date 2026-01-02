package com.javaquery.opencsv.model;

import com.javaquery.annotations.Exportable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;
import net.datafaker.Faker;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Exportable(key = "firstName")
    private String firstName;

    @Exportable(key = "lastName")
    private String lastName;

    @Exportable(key = "email")
    private String email;

    @Exportable(key = "age")
    private Integer age;

    @Exportable(key = "about")
    private String about;

    @Exportable(key = "passport")
    private Passport passport;

    @Exportable(key = "addresses")
    private Set<Address> addresses;

    public static List<Customer> fakeData(int count, boolean withAddress) {
        Faker faker = new Faker();
        return faker.collection()
                .len(count)
                .suppliers(() -> createCustomer(faker, withAddress))
                .build()
                .get();
    }

    private static Customer createCustomer(Faker faker, boolean withAddress) {
        Customer.CustomerBuilder builder = Customer.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(1, 100))
                .passport(Passport.fakeData());
        if (withAddress) {
            builder.addresses(new HashSet<>(Address.fakeData(4)));
        }
        return builder.build();
    }
}
