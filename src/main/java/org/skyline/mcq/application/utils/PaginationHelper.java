package org.skyline.mcq.application.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class PaginationHelper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int MAX_PAGE_SIZE = 1000;

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE;
        }

        if (pageSize == null) {
            queryPageSize = DEFAULT_PAGE_SIZE;
        } else {
            if (pageSize > MAX_PAGE_SIZE) {
                queryPageSize = MAX_PAGE_SIZE;
            } else {
                queryPageSize = pageSize;
            }
        }

        return org.springframework.data.domain.PageRequest.of(queryPageNumber, queryPageSize);
    }
}
