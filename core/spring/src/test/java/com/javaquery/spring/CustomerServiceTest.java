package com.javaquery.spring;

import com.javaquery.spring.data.PageData;
import com.javaquery.spring.model.Customer;
import com.javaquery.spring.service.CustomerService;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    public void setup() {
        customerService.deleteAll();
    }

    @Test
    void saveTest() {
        List<Customer> customers = Customer.fakeData(5);
        customers.forEach(customerService::save);

        PageData<Customer> savedCustomers = customerService.findAll(Pageable.unpaged());
        assert savedCustomers.getTotalElements() == 5;
    }

    @Test
    void saveAllTest() {
        List<Customer> customers = Customer.fakeData(10);
        customerService.saveAll(customers);

        PageData<Customer> savedCustomers = customerService.findAll(Pageable.unpaged());
        assert savedCustomers.getTotalElements() == 10;
    }

    @Test
    void findByIdTest() {
        Customer customer = Customer.fakeData(1).get(0);
        Customer savedCustomer = customerService.save(customer);

        Customer fetchedCustomer = customerService.findById(savedCustomer.getId(), null);
        assert fetchedCustomer != null;
        assert fetchedCustomer.getId().equals(savedCustomer.getId());
    }

    @Test
    void findByIdNotFoundTest() {
        Customer fetchedCustomer = customerService.findById(999L, null);
        assert fetchedCustomer == null;
    }

    @Test
    void findByIdNotFoundWithExceptionTest() {
        try {
            customerService.findById(999L, () -> {
                throw new RuntimeException("Record not found with id: 999");
            });
            assert false; // Should not reach here
        } catch (Exception e) {
            assert e.getMessage().contains("Record not found with id: 999");
        }
    }

    @Test
    void deleteByIdTest() {
        Customer customer = Customer.fakeData(1).get(0);
        Customer savedCustomer = customerService.save(customer);

        customerService.deleteById(savedCustomer.getId(), null);

        Customer fetchedCustomer = customerService.findById(savedCustomer.getId(), null);
        assert fetchedCustomer == null;
    }

    @Test
    void deleteByIdNotFoundTest() {
        Customer fetchedCustomer = customerService.deleteById(999L, null);
        assert fetchedCustomer == null;
    }

    @Test
    void deleteByIdWithExceptionTest() {
        try {
            customerService.deleteById(999L, () -> {
                throw new RuntimeException("Record not found with id: 999");
            });
            assert false; // Should not reach here
        } catch (Exception e) {
            assert e.getMessage().contains("Record not found with id: 999");
        }
    }

    @Test
    void deleteTest() {
        Customer customer = Customer.fakeData(1).get(0);
        Customer savedCustomer = customerService.save(customer);

        customerService.delete(savedCustomer);

        Customer fetchedCustomer = customerService.findById(savedCustomer.getId(), null);
        assert fetchedCustomer == null;
    }

    @Test
    void existsByIdTest() {
        Customer customer = Customer.fakeData(1).get(0);
        Customer savedCustomer = customerService.save(customer);
        boolean exists = customerService.existsById(savedCustomer.getId(), null);
        assert exists;
    }

    @Test
    void existsByIdNotFoundTest() {
        boolean exists = customerService.existsById(999L, null);
        assert !exists;
    }

    @Test
    void existsByIdWithExceptionTest() {
        try {
            customerService.existsById(999L, () -> {
                throw new RuntimeException("Record not found with id: 999");
            });
            assert false; // Should not reach here
        } catch (Exception e) {
            assert e.getMessage().contains("Record not found with id: 999");
        }
    }

    @Test
    void findAllByIdTest() {
        List<Customer> customers = Customer.fakeData(5);
        List<Customer> savedCustomers = customerService.saveAll(customers);

        List<Long> ids = savedCustomers.stream().map(Customer::getId).collect(Collectors.toList());
        List<Customer> fetchedCustomers = customerService.findAllById(ids);

        assert fetchedCustomers.size() == 5;
    }

    @Test
    void findAllByIdEmptyTest() {
        List<Customer> fetchedCustomers = customerService.findAllById(List.of());
        assert fetchedCustomers.isEmpty();
    }

    @Test
    void findAllTest() {
        List<Customer> customers = Customer.fakeData(7);
        customerService.saveAll(customers);

        PageData<Customer> fetchedCustomers = customerService.findAll(Pageable.unpaged());
        assert fetchedCustomers.getTotalElements() == 7;
    }

    @Test
    void findAllEmptyTest() {
        PageData<Customer> fetchedCustomers = customerService.findAll(Pageable.unpaged());
        assert fetchedCustomers.getTotalElements() == 0;
    }

    @Test
    void findAllSpecificationUnpagedTest() {
        List<Customer> customers = Customer.fakeData(10);
        customerService.saveAll(customers);

        Customer customer = customers.get(0);
        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer
                .equal("firstName", customer.getFirstName())
                .and(specificationCustomer.equal("lastName", customer.getLastName()))
                .and(specificationCustomer.equal("email", customer.getEmail()));

        PageData<Customer> fetchedCustomers = customerService.findAll(specification, Pageable.unpaged());
        // Assuming some customers have firstName containing 'John' and isActive true
        assert fetchedCustomers.getTotalElements() == 1;
    }

    @Test
    void findAllBySpecificationUnpagedEmptyTest() {
        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer.equal("firstName", "NonExistentName");

        PageData<Customer> fetchedCustomers = customerService.findAll(specification, Pageable.unpaged());
        assert fetchedCustomers.getTotalElements() == 0;
    }

    @Test
    void findAllSpecificationPageableTest() {
        List<Customer> customers = Customer.fakeData(25);
        customers.stream().skip(15).forEach(customer -> customer.setFirstName("a-" + customer.getFirstName()));
        customerService.saveAll(customers);

        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer.startsWith("firstName", "a-");

        Pageable pageable = Pageable.ofSize(5).withPage(0);
        PageData<Customer> fetchedCustomers = customerService.findAll(specification, pageable);
        assert fetchedCustomers.getData().size() == 5;
        assert fetchedCustomers.getTotalElements() == 10;
    }

    @Test
    void findAllSpecificationTest() {
        List<Customer> customers = Customer.fakeData(20);
        customers.stream().skip(10).forEach(customer -> customer.setLastName(customer.getLastName() + "-b"));
        customerService.saveAll(customers);

        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer.endsWith("lastName", "-b");

        List<Customer> fetchedCustomers = customerService.findAll(specification);
        assert fetchedCustomers.size() == 10;
    }

    @Test
    void findAllSpecificationLikeTest() {
        List<Customer> customers = Customer.fakeData(15);
        customers.stream().skip(5).forEach(customer -> customer.setEmail("test+" + customer.getEmail()));
        customerService.saveAll(customers);

        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer.like("email", "test+%");

        List<Customer> fetchedCustomers = customerService.findAll(specification);
        assert fetchedCustomers.size() == 10;
    }

    @Test
    void findAllSpecificationNotEqualsTest() {
        List<Customer> customers = Customer.fakeData(12);
        customers.get(0).setFirstName("rrrrrrrrr");
        customerService.saveAll(customers);

        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer.notEqual("firstName", "rrrrrrrrr");

        List<Customer> fetchedCustomers = customerService.findAll(specification);
        assert fetchedCustomers.size() == 11;
    }

    @Test
    void findAllSpecificationInTest() {
        List<Customer> customers = Customer.fakeData(8);
        customerService.saveAll(customers);

        List<String> firstNames =
                customers.stream().limit(3).map(Customer::getFirstName).collect(Collectors.toList());

        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer.in("firstName", firstNames);

        List<Customer> fetchedCustomers = customerService.findAll(specification);
        assert fetchedCustomers.size() == 3;
    }

    @Test
    void findAllSpecificationNotInTest() {
        List<Customer> customers = Customer.fakeData(10);
        customerService.saveAll(customers);

        List<String> firstNames =
                customers.stream().limit(4).map(Customer::getFirstName).collect(Collectors.toList());

        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer.notIn("firstName", firstNames);

        List<Customer> fetchedCustomers = customerService.findAll(specification);
        assert fetchedCustomers.size() == 6;
    }

    @Test
    void findAllSpecificationContainsTest() {
        List<Customer> customers = Customer.fakeData(14);
        customers.stream().skip(7).forEach(customer -> customer.setEmail("sample+" + customer.getEmail()));
        customerService.saveAll(customers);

        Customer specificationCustomer = new Customer();
        Specification<Customer> specification = specificationCustomer.contains("email", "sample+");

        List<Customer> fetchedCustomers = customerService.findAll(specification);
        assert fetchedCustomers.size() == 7;
    }

    @Test
    void findAllPageableTest() {
        List<Customer> customers = Customer.fakeData(30);
        customerService.saveAll(customers);

        Pageable pageable = Pageable.ofSize(10).withPage(2);
        PageData<Customer> fetchedCustomers = customerService.findAll(pageable);
        assert fetchedCustomers.getData().size() == 10;
        assert fetchedCustomers.getTotalElements() == 30;
    }

    @Test
    void countTest() {
        List<Customer> customers = Customer.fakeData(18);
        customerService.saveAll(customers);

        long count = customerService.count();
        assert count == 18;
    }
}
