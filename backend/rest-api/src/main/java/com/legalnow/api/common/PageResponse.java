package com.legalnow.api.common;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PageResponse<T>(
    List<T> items,
    int page,
    int size,
    @JsonProperty("total_elements") long totalElements,
    @JsonProperty("total_pages") int totalPages
) {
    public static <S, T> PageResponse<T> from(Page<S> source, Function<S, T> mapper) {
        return new PageResponse<>(
            source.getContent().stream().map(mapper).toList(),
            source.getNumber(),
            source.getSize(),
            source.getTotalElements(),
            source.getTotalPages()
        );
    }
}
