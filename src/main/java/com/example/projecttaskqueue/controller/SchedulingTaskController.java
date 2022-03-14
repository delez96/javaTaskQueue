package com.example.projecttaskqueue.controller;


import com.example.projecttaskqueue.dao.TaskQueueDao;
import com.example.projecttaskqueue.dao.TaskDao;
import com.example.projecttaskqueue.dto.QueueStatus;
import com.example.projecttaskqueue.service.TaskQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;

@RestController
@Slf4j
public class SchedulingTaskController {

    @Autowired
    TaskQueueService taskQueueService;

    @GetMapping("/queue")
    public ResponseEntity<String> allQueue() {
        return new ResponseEntity<>(taskQueueService.getAllQueue(), HttpStatus.OK);
    }

    @PostMapping("/queue")
    public ResponseEntity<String> addQueue(@RequestBody TaskQueueDao newQueueTask) {
        return new ResponseEntity<>(taskQueueService.saveQueue(newQueueTask), HttpStatus.OK);
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable("id") Long id, @RequestParam("queueStatus") QueueStatus queueStatus) {
        return taskQueueService.changeStatus(id, queueStatus);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Norm destroy");
    }

    @GetMapping(value = "/{id}/task", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void getUrl(@PathVariable("id") Long id, @RequestBody TaskDao requestBody) {
        log.info(String.valueOf(requestBody));
        taskQueueService.addTask(requestBody, id);
    }
}