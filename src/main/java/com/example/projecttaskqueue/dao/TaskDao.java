package com.example.projecttaskqueue.dao;

import com.example.projecttaskqueue.dto.MethodType;
import com.example.projecttaskqueue.dto.TaskStatus;
import lombok.*;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
public class TaskDao extends Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nameTask;
    private String url;
    private MethodType method;
    @Transient
    private Map<String, String> headers;
    private String body;
    private String blockTask;
    private TaskStatus taskStatus = TaskStatus.CREATED;
    private String message;
    private int requestCode;
    private double executionTime;


    @ManyToOne
    private TaskQueueDao queueTask;

    public TaskDao(String name, String url, MethodType method, Map<String, String> headers, String body, String blockTask) {
        this.nameTask = name;
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.blockTask = blockTask;
    }


    @Override
    public String toString() {
        return "Request{" +
                "nameTask='" + nameTask + '\'' +
                ", url='" + url + '\'' +
                ", method=" + method +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", blockTask='" + blockTask + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
