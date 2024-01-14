package com.example.project01.config.batch;

import com.example.project01.youtube.entity.UserEntity;
import com.example.project01.youtube.model.YoutubeContent;
import com.example.project01.youtube.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig extends DefaultBatchConfigurer {
    private final YoutubeContentProcessor youtubeContentProcessor;

    private final YoutubeInfoWriter youtubeInfoWriter;

    private final UserRepository userRepository;

    @Override
    public void setDataSource(DataSource dataSource) {
    }


    @Scheduled(cron = "0 0 17 * * *")
    public void jobScheduling() {
        try {
            StopWatch watch = new StopWatch();
            log.info("Youtube Content Crawling start :)");
            watch.start();
            JobParameter parameter = new JobParameter(Date.valueOf(LocalDate.now()));
            getJobLauncher().run(youtubeJob(), new JobParameters(Map.of("date", parameter)));
            watch.stop();
            log.info("Youtube Content Crawling end :) time: {}", watch.getTotalTimeSeconds());
        } catch (Exception exception) {
            log.debug("{}", exception.getMessage());
        }
    }

    @Bean
    public Job youtubeJob() {
        return new JobBuilder("youtubeCrawling")
                .preventRestart()
                .repository(getJobRepository())
                .start(youtubeCrawlingStep())
                .build();
    }

    @Bean
    public Step youtubeCrawlingStep() {
        return new StepBuilder("youtubeCrawlingStep")
                .repository(getJobRepository())
                .transactionManager(getTransactionManager())
                .<UserEntity, YoutubeContent>chunk(10)
                .reader(itemReader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader itemReader() {
        return new RepositoryItemReaderBuilder()
                .name("itemReader")
                .methodName("getUserEntitiesBy")
                .repository(userRepository)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public AsyncItemProcessor asyncItemProcessor() {
        AsyncItemProcessor<UserEntity, List<YoutubeContent>>  asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(youtubeContentProcessor);
        asyncItemProcessor.setTaskExecutor(taskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter<List<YoutubeContent>> asyncItemWriter() {
        AsyncItemWriter<List<YoutubeContent>> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(youtubeInfoWriter);
        return asyncItemWriter;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        return taskExecutor;
    }
}
