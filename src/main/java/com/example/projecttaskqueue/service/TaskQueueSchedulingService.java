package com.example.projecttaskqueue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TaskQueueSchedulingService {

    @Autowired
    private ThreadPoolFreeHandlerService threadPoolFreeHandlerService;

    @Scheduled(fixedDelay = 10L, initialDelay = 1L, timeUnit = TimeUnit.SECONDS)
    public void checkFreeTask(){
        threadPoolFreeHandlerService.checkFreeTaskTransactional();
    }
}
