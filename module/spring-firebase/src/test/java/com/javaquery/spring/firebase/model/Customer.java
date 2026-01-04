package com.javaquery.spring.firebase.model;

import com.javaquery.annotations.Exportable;
import java.util.List;
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

    public static List<Customer> fakeData(int count) {
        Faker faker = new Faker();
        return faker.collection()
                .len(count)
                .suppliers(() -> createCustomer(faker))
                .build()
                .get();
    }

    private static Customer createCustomer(Faker faker) {
        CustomerBuilder builder = Customer.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(1, 100));
        return builder.build();
    }
}
