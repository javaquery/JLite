package com.javaquery.spring.data;

import java.util.List;
import lombok.Getter;

/**
 * Generic class to hold paginated data along with pagination metadata.
 * @author vicky.thakor
 * @since 1.0.0
 * @param <T> the type of data contained in the page
 */
@Getter
public class PageData<T> {
    private final long totalElements;
    private final int totalPages;
    private final int currentPage;
    private final int pageSize;
    private final List<T> data;

    /**
     * Constructor to initialize all fields.
     *
     * @param totalElements total number of elements
     * @param totalPages total number of pages
     * @param currentPage current page number
     * @param pageSize size of each page
     * @param data list of data elements for the current page
     */
    public PageData(long totalElements, int totalPages, int currentPage, int pageSize, List<T> data) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.data = data;
    }
}
