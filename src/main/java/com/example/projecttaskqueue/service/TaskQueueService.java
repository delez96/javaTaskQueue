package com.example.projecttaskqueue.service;

import com.example.projecttaskqueue.dao.TaskQueueDao;
import com.example.projecttaskqueue.dao.TaskDao;
import com.example.projecttaskqueue.dto.QueueStatus;
import com.example.projecttaskqueue.dto.TaskStatus;
import com.example.projecttaskqueue.repo.TaskQueueRepo;
import com.example.projecttaskqueue.repo.TaskRepo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Data
@Component
@NoArgsConstructor
public class TaskQueueService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private TaskQueueRepo taskQueueRepo;

    @Autowired
    private ObjectProvider<TaskService> requestTaskServiceObjectProvider;

    @Transactional
    public String getAllQueue() {
        return taskQueueRepo.findAll().toString();
    }

    @Transactional
    public String saveQueue(TaskQueueDao taskQueueDao) {
        taskQueueRepo.save(taskQueueDao);
        return taskQueueDao.toString();
    }


    @Transactional
    public void addTask(TaskDao request, Long id) {

        if (request.getNameTask() == null || taskRepo.findByNameTaskAndQueueTaskId(request.getNameTask(), id).orElse(null) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name can't be null or contains existent task");
        }
        if (request.getBlockTask() != null &&
                taskRepo.findByNameTaskAndQueueTaskId(request.getBlockTask(), id).orElse(null) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Block task contains a non-existent task");
        }
        request.setQueueTask(taskQueueRepo.getById(id));
        if (request.getBlockTask() != null) {
            TaskStatus requestDaoTaskStatus = taskRepo.getRequestDaoByNameTaskAndQueueTask(request.getBlockTask(), taskQueueRepo.getById(id)).getTaskStatus();
            if (requestDaoTaskStatus == TaskStatus.IN_PROCESS || requestDaoTaskStatus == TaskStatus.CREATED) {
                request.setTaskStatus(TaskStatus.WAITING);
            }
        }
        taskRepo.save(request);
    }

    @Transactional
    public String changeStatus(Long id, QueueStatus queueStatus) {

        taskQueueRepo.setStatusById(queueStatus, id);
        if (queueStatus == QueueStatus.DELETED) {
            taskRepo.setTaskStatusByQueueTaskId(TaskStatus.CANCELED, taskQueueRepo.getById(id), TaskStatus.CREATED, TaskStatus.WAITING);
        }
        if (queueStatus == QueueStatus.OPEN) {
            taskRepo.setTaskStatusByQueueTaskIdWithBlockTask1(TaskStatus.WAITING, TaskStatus.CREATED, taskQueueRepo.getById(id), TaskStatus.CANCELED);
        }
        return taskQueueRepo.getById(id).toString();
    }
}
