package com.healthcare.hospital.modules.operations;

import java.util.List;
import java.util.Objects;

public record OperationPage<T>(int page, int size, long totalItems, List<T> items) {
    public OperationPage {
        if (page < 0) throw new IllegalArgumentException("page must be non-negative");
        if (size < 1 || size > 500) throw new IllegalArgumentException("size must be between 1 and 500");
        if (totalItems < 0) throw new IllegalArgumentException("totalItems must be non-negative");
        items = List.copyOf(Objects.requireNonNull(items, "items"));
    }
    public int totalPages() {
        if (totalItems == 0) return 0;
        return (int) Math.min(Integer.MAX_VALUE, (totalItems + size - 1) / size);
    }
    public boolean hasNext() { return page + 1 < totalPages(); }
    public boolean hasPrevious() { return page > 0; }
    public int firstItemIndex() { return totalItems == 0 ? 0 : page * size + 1; }
    public int lastItemIndex() { return Math.min((page + 1) * size, (int) Math.min(Integer.MAX_VALUE, totalItems)); }
}
