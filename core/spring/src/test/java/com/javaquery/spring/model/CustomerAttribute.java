package com.javaquery.spring.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(
        of = {"customerId", "name"},
        callSuper = false)
@Entity
@Table(
        name = "customerAttributes",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"customerId", "name"})})
@DynamicInsert
@DynamicUpdate
public class CustomerAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customerId")
    private Long customerId;

    @Column(name = "name")
    private String name;

    @Column(name = "val")
    private String val;
}
