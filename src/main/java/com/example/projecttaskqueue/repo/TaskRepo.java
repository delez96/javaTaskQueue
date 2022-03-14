package com.example.projecttaskqueue.repo;


import com.example.projecttaskqueue.dao.TaskQueueDao;
import com.example.projecttaskqueue.dao.TaskDao;
import com.example.projecttaskqueue.dto.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<TaskDao, Long> {
    Optional<TaskDao> findByNameTaskAndQueueTaskId(String name, Long id);

    @Modifying
    @Query("UPDATE TaskDao r set r.taskStatus = ?1 where r.queueTask = ?2 AND (r.taskStatus = ?3 or r.taskStatus = ?4)")
    void setTaskStatusByQueueTaskId(TaskStatus afterStatus, TaskQueueDao queueTask, TaskStatus beforeStatus, TaskStatus beforeStatus2);

    @Modifying
    @Query("UPDATE TaskDao r set r.taskStatus = " +
            "CASE " +
                 "WHEN r.blockTask is not null THEN ?1 " +
                 "WHEN r.blockTask is null THEN ?2 " +
                 "ELSE r.taskStatus " +
            "END " +
            "where r.queueTask = ?3 AND r.taskStatus = ?4")
    void setTaskStatusByQueueTaskIdWithBlockTask1(TaskStatus afterStatus, TaskStatus afterStatus2, TaskQueueDao queueTask, TaskStatus beforeStatus);

    @Modifying
    @Query("UPDATE TaskDao r set r.taskStatus = ?1 where r.id = ?2")
    @Transactional
    void setTaskStatusById(TaskStatus taskStatus, Long id);

    List<TaskDao> findByTaskStatus(TaskStatus created);

    TaskDao getRequestDaoByNameTaskAndQueueTask(String nameTask, TaskQueueDao taskQueueDao);

    List<TaskDao> getRequestDaoByTaskStatus(TaskStatus taskStatus);

    @Modifying
    @Query("UPDATE TaskDao r set r.message = ?1, r.requestCode = ?2, r.executionTime = ?3 where r.id = ?4")
    @Transactional
    void setMessageById(String server_off, int code, double executionTimeSecond, long id);
}
