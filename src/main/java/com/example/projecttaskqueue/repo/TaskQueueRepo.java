package com.example.projecttaskqueue.repo;

import com.example.projecttaskqueue.dao.TaskQueueDao;
import com.example.projecttaskqueue.dto.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TaskQueueRepo extends JpaRepository<TaskQueueDao, Long> {

    @Modifying
    @Query("update TaskQueueDao q set q.queueStatus = ?1 where q.id = ?2")
    void setStatusById(QueueStatus queueStatus, Long id);

}
