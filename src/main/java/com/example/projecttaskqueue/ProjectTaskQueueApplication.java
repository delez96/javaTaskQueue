package com.example.projecttaskqueue;

import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class ProjectTaskQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectTaskQueueApplication.class, args);
    }
    @Bean
    public OkHttpClient client(){
        return new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
    }
}
