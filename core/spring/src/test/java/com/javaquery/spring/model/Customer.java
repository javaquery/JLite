package com.javaquery.spring.model;

import com.javaquery.spring.repository.AbstractSpecification;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import net.datafaker.Faker;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "customers",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
public class Customer implements AbstractSpecification<Customer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstName", nullable = false, length = 50)
    private String firstName;

    @Column(name = "lastName", length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @CreatedDate
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modifiedAt", nullable = false)
    private LocalDateTime modifiedAt;

    public static List<Customer> fakeData(int count) {
        Faker faker = new Faker();
        return faker.collection()
                .len(count)
                .suppliers(() -> createCustomer(faker))
                .build()
                .get();
    }

    private static Customer createCustomer(Faker faker) {
        Customer.CustomerBuilder builder = Customer.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress());
        return builder.build();
    }
}
