package com.example.project01.youtube.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagingResponse<T> implements Serializable {
    private int page;
    private int size;
    private List<T> data;

    public PagingResponse<T> from(int page, int size, List<T> data) {
        PagingResponse pagingResponse = new PagingResponse();
        pagingResponse.setPage(page);
        pagingResponse.setSize(size);
        pagingResponse.setData(data);
        return pagingResponse;
    }
}
