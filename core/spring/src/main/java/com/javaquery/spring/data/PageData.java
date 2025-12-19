package com.javaquery.spring.data;

import java.util.List;
import lombok.Getter;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
@Getter
public class PageData<T> {
    private final long totalElements;
    private final int totalPages;
    private final int currentPage;
    private final int pageSize;
    private final List<T> data;

    public PageData(long totalElements, int totalPages, int currentPage, int pageSize, List<T> data) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.data = data;
    }
}
