package com.example.projecttaskqueue.service;

import com.example.projecttaskqueue.dao.TaskDao;
import com.example.projecttaskqueue.dto.TaskStatus;
import com.example.projecttaskqueue.repo.TaskRepo;
import lombok.Setter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Component
@Scope(value = "prototype")
public class TaskService implements Runnable{

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private OkHttpClient client;

    Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Setter
    private TaskDao taskDao;

    @Override
    public void run() {
        logger.info("Starting :" + String.valueOf(taskDao));
        try {
            okhttp3.RequestBody body = okhttp3.RequestBody.create(taskDao.getBody(), okhttp3.MediaType.get(MediaType.APPLICATION_JSON_VALUE));
            String url = taskDao.getUrl();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .method(String.valueOf(taskDao.getMethod()), body.contentLength() == 0 ? null : body)
                    .build();
            Call call = client.newCall(request);
            try {
                taskRepo.setTaskStatusById(TaskStatus.IN_PROCESS, taskDao.getId());
                double start = (double) System.currentTimeMillis();
                Response response = call.execute();
                double finish = (double) System.currentTimeMillis();
                double elapsed = (finish - start) / 1000;
                JSONObject jsonObject = new JSONObject(response.body().string());
                taskRepo.setMessageById(String.valueOf(jsonObject.get("message")), (Integer) jsonObject.get("status"),
                        elapsed, taskDao.getId());
                taskRepo.setTaskStatusById(TaskStatus.FINISHED, taskDao.getId());
            } catch (ConnectException e) {
                taskRepo.setTaskStatusById(TaskStatus.FAILED, taskDao.getId());
                taskRepo.setMessageById("server off", 500, 0, taskDao.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Finished :" + String.valueOf(taskDao));

    }
}
