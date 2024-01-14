package com.example.project01.config.batch;

import com.example.project01.youtube.mapper.YoutubeContentMapper;
import com.example.project01.youtube.model.YoutubeContent;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class YoutubeInfoWriter implements ItemWriter<List<YoutubeContent>> {

    private final YoutubeContentMapper youtubeContentMapper;

    @Override
    public void write(List<? extends List<YoutubeContent>> youtubeContentsList) throws Exception {
        youtubeContentsList.forEach((youtubeContents) -> youtubeContents.forEach(youtubeContentMapper::save));
    }
}
