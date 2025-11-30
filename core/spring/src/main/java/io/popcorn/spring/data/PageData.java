package io.popcorn.spring.data;

import java.util.List;

/**
 * @author vicky.thakor
 * @since 1.0.0
 */
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

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getData() {
        return data;
    }
}
