package com.example.project01.youtube.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseV1 implements Serializable {
    Integer status;
    String error_message;
    Object data;
    LocalDateTime time;

    public static ResponseV1 ok(Object result) {
        return ResponseV1.builder()
                .status(HttpStatus.OK.value())
                .data(result)
                .time(LocalDateTime.now())
                .build();
    }

    public static ResponseV1 error(HttpStatus status, String message) {
        return ResponseV1.builder()
                .status(status.value())
                .error_message(message)
                .time(LocalDateTime.now())
                .build();
    }
}
