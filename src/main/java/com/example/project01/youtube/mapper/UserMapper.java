package com.example.project01.youtube.mapper;

import com.example.project01.youtube.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    void save(@Param("param") User user);
}
