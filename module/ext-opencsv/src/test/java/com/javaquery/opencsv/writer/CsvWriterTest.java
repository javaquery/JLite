package com.javaquery.opencsv.writer;

import com.javaquery.opencsv.model.Address;
import com.javaquery.opencsv.model.Customer;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

/**
 * @author vicky.thakor
 * @since 2025-12-09
 */
public class CsvWriterTest {

    @Test
    public void testWriteCsv() throws IOException {
        File tempFile = File.createTempFile("customers", ".csv");
        CsvWriter.<Customer>builder()
                .headers(List.of("First Name", "Last Name", "Email", "Address Line 1", "City", "State"))
                .keys(List.of(
                        "firstName",
                        "lastName",
                        "email",
                        "addresses.addressLine1",
                        "addresses.city",
                        "addresses.state"))
                .data(customerDummyData())
                .toFile(tempFile)
                .write();
    }

    public List<Customer> customerDummyData() {
        Faker faker = new Faker();
        return faker.collection()
                .len(10)
                .suppliers(() -> createCustomer(faker))
                .build()
                .get();
    }

    private Customer createCustomer(Faker faker) {
        List<Address> addresses = faker.collection()
                .len(2)
                .suppliers(() -> createAddress(faker))
                .build()
                .get();

        return Customer.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .addresses(new HashSet<>(addresses))
                .build();
    }

    private Address createAddress(Faker faker) {
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
