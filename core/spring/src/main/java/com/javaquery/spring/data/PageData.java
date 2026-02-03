package com.javaquery.spring.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Generic class to hold paginated data along with pagination metadata.
 * @author vicky.thakor
 * @since 1.0.0
 * @param <T> the type of data contained in the page
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private List<T> data;
}
