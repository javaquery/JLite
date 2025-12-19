package com.javaquery.spring;

import com.javaquery.spring.model.CustomerAttribute;
import com.javaquery.spring.service.CustomerAttributeService;
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
public class CustomerAttributeServiceTest {

    @Autowired
    private CustomerAttributeService customerAttributeService;

    @Test
    void findAllSpecificationPageableExecutorNotExtended() {
        try {
            Specification<CustomerAttribute> specification =
                    (root, query, cb) -> cb.equal(root.get("attributeKey"), "key1");
            customerAttributeService.findAll(specification, Pageable.unpaged());
        } catch (UnsupportedOperationException ex) {
            assert ex.getMessage().equals("Repository does not support Specifications.");
        }
    }

    @Test
    void findAllSpecificationExecutorNotExtended() {
        try {
            Specification<CustomerAttribute> specification =
                    (root, query, cb) -> cb.equal(root.get("attributeKey"), "key1");
            customerAttributeService.findAll(specification);
        } catch (UnsupportedOperationException ex) {
            assert ex.getMessage().equals("Repository does not support Specifications.");
        }
    }
}
