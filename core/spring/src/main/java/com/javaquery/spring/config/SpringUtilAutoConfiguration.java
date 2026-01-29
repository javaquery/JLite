package com.javaquery.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.javaquery.spring.service.ObjectMapperService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Spring utilities.
 * This configuration will automatically be picked up by Spring Boot when the module is on the classpath.
 *
 * @author vicky.thakor
 * @since 1.0.1
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
public class SpringUtilAutoConfiguration {

    /**
     * Creates a snake_case ObjectMapper bean if one doesn't already exist.
     *
     * @return ObjectMapper configured with snake_case naming strategy
     */
    @Bean
    @Qualifier("snakeCaseObjectMapper")
    @ConditionalOnMissingBean(name = "snakeCaseObjectMapper")
    public ObjectMapper snakeCaseObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper;
    }

    /**
     * Creates an ObjectMapperService bean if one doesn't already exist.
     * This bean will only be created if ObjectMapper is available in the context.
     *
     * @param objectMapper          the default ObjectMapper bean
     * @param snakeCaseObjectMapper the snake_case ObjectMapper bean
     * @return ObjectMapperService instance
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapperService objectMapperService(
            ObjectMapper objectMapper, @Qualifier("snakeCaseObjectMapper") ObjectMapper snakeCaseObjectMapper) {
        return new ObjectMapperService(objectMapper, snakeCaseObjectMapper);
    }
}
