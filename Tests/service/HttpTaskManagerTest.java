package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import server.KVServer;

import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    KVServer kvServer;

    @Override
    void init() {
        taskManager = new HttpTaskManager("http://localhost:8078");
    }

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        init();
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
    }

    @Test
    void firstLoadToServerThanLoadFromServer() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        Task task1 = taskManager.createTask(new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9)));
        Epic epic2 = taskManager.createEpic(new Epic("Epic #2", "DE"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("SubTask #3", "DS", Status.NEW, 2, startTime1.plusMinutes(20), Duration.ofMinutes(6)));
        taskManager.getTaskById(1);

        TaskManager manager2 = Managers.getDefault();
        List<Task> history = manager2.getHistory();
        assertEquals(task1, manager2.getTaskById(1), "Tasks should be equal");
        assertEquals(epic2, manager2.getEpicById(2), "Epics should be equal");
        assertEquals(subTask3, manager2.getSubTaskById(3), "Sub tasks should be equal");
        assertEquals(1, history.size(), "History should contain 1 element");
    }
}