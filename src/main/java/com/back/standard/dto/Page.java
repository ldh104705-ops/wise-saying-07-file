package com.back.standard.dto;

import java.util.List;

/**
 * 페이징 결과.
 */
public record Page<T>(
        List<T> content,
        int totalCount,
        int page,
        int pageSize
) {
    public int getTotalPages() {
        if (totalCount == 0) return 1;

        return (int) Math.ceil((double) totalCount / pageSize);
    }
}
