package com.example.projecttaskqueue.service;

import com.example.projecttaskqueue.dao.TaskQueueDao;
import com.example.projecttaskqueue.dao.TaskDao;
import com.example.projecttaskqueue.dto.TaskStatus;
import com.example.projecttaskqueue.repo.TaskQueueRepo;
import com.example.projecttaskqueue.repo.TaskRepo;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ThreadPoolFreeHandlerService {

    @Autowired
    TaskRepo taskRepo;

    @Autowired
    private ObjectProvider<TaskService> requestTaskServiceObjectProvider;

    @Autowired
    TaskQueueRepo taskQueueRepo;

    private Map<Long, ThreadPoolExecutor> threadPool = new HashMap<>();


    @Transactional
    public void checkFreeTaskTransactional() {
        checkThreadPoolTask();
        checkDelayTask();
        List<TaskDao> taskDaoList = taskRepo.findByTaskStatus(TaskStatus.CREATED);
        for (Map.Entry<Long, ThreadPoolExecutor> threadPoolEx : this.threadPool.entrySet()) {
            List<TaskDao> taskDaoListInQueue = taskDaoList.stream().
                    filter(o -> o.getQueueTask().getId() == threadPoolEx.getKey()).collect(Collectors.toList());
            int freeCountThread = threadPoolEx.getValue().getCorePoolSize() - threadPoolEx.getValue().getActiveCount();
            int currentThreadCount = Math.min(freeCountThread, taskDaoListInQueue.size());
            for (int i = 0; i < currentThreadCount; i++) {
                TaskDao taskDao = taskDaoListInQueue.get(i);
                addTaskToThreadPool(taskDao, threadPoolEx.getValue());
            }
        }
    }

    private void checkThreadPoolTask() {
        List<TaskQueueDao> taskQueueDaos = taskQueueRepo.findAll();
        for (TaskQueueDao queueD : taskQueueDaos) {
            if (!threadPool.containsKey(queueD.getId())){
                threadPool.put(queueD.getId(),
                        new ThreadPoolExecutor(queueD.getMaxThread(), queueD.getMaxThread(), Long.MAX_VALUE,
                                TimeUnit.SECONDS, new LinkedTransferQueue<>())
                );

            }
        }
    }

    private void checkDelayTask() {
        List<TaskDao> taskDaoList = taskRepo.getRequestDaoByTaskStatus(TaskStatus.WAITING);
        for (TaskDao request : taskDaoList) {
            TaskStatus requestDaoTaskStatus = taskRepo.getRequestDaoByNameTaskAndQueueTask(request.getBlockTask(), request.getQueueTask()).getTaskStatus();
            if (requestDaoTaskStatus == TaskStatus.FINISHED) {
                taskRepo.setTaskStatusById(TaskStatus.CREATED, request.getId());
            }
        }
    }

    public void addTaskToThreadPool(TaskDao taskDao, ThreadPoolExecutor threadPoolEx){
        TaskService taskService = requestTaskServiceObjectProvider.getObject();
        taskService.setTaskDao(taskDao);
        threadPoolEx.submit(taskService);
    }
}
