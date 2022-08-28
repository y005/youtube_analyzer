package com.example.project01.youtube.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class PagingCondition {
    private Integer offset;
    private Integer size;

    public void init() {
        offset = offset != null ? offset : 0;
        size = size != null ? size : 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagingCondition that = (PagingCondition) o;
        return Objects.equals(offset, that.offset) && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, size);
    }
}
