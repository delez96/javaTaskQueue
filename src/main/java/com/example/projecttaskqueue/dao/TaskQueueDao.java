package com.example.projecttaskqueue.dao;

import com.example.projecttaskqueue.dto.QueueStatus;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class TaskQueueDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "queueTask", fetch = FetchType.EAGER)
    private List<TaskDao> tasks = new ArrayList<>();

    private QueueStatus queueStatus;

    private int maxThread = 3;

    @Override
    public String toString() {
        return "QueueTask{\n" +
                "\tid=" + id +
                "\ttasks=" + tasks +
                ",\n \tstatus=" + queueStatus +
                ",\n \tmaxThread=" + maxThread +
                "}\n";
    }
}
