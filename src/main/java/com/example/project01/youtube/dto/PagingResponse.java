package com.example.project01.youtube.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagingResponse<T> {
    private int offset;
    private int maxSize;
    private int size;
    private List<T> data;

    public PagingResponse<T> from(PagingCondition pagingCondition, List<T> data) {
        PagingResponse pagingResponse = new PagingResponse();
        pagingResponse.setOffset(pagingCondition.getOffset());
        pagingResponse.setMaxSize(pagingCondition.getSize());
        pagingResponse.setSize(data.size());
        pagingResponse.setData(data);
        return pagingResponse;
    }
}
