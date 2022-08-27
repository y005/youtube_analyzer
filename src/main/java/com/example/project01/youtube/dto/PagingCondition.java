package com.example.project01.youtube.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PagingCondition {
    private Integer offset;
    private Integer size;

    public void init() {
        offset = offset != null ? offset : 0;
        size = size != null ? size : 10;
    }
}
