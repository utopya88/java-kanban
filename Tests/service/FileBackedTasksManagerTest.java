package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    Path somePath = Paths.get("test.csv");

    @Override
    void init() {
        taskManager = new FileBackedTasksManager(Managers.getDefaultHistory(), somePath);
    }

    @BeforeEach
    void beforeEach() {
        init();
    }

    @Test
    void saveNewRecordInFile() {
        try {
            Files.deleteIfExists(somePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        Task task1 = new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        taskManager.createTask(task1);
        assertTrue(Files.exists(somePath));
    }

    @Test
    void loadFromFile() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Task task1 = taskManager.createTask(new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9)));
        Epic epic2 = taskManager.createEpic(new Epic("Epic #2", "DT"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("SubTask #3", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(9)));
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubTaskById(3);

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(somePath.toFile());
        assertEquals(3, newTaskManager.getHistory().size(), "History should store 3 elements");
        assertEquals(task1, newTaskManager.getTaskById(1), "Task is not equal");
        assertEquals(epic2, newTaskManager.getEpicById(2), "Epic is not equal");
        assertEquals(subTask3, newTaskManager.getSubTaskById(3), "SubTask is not equal");
        assertEquals(2, newTaskManager.getPrioritizedTasks().size(), "Should contain 2 elements");
    }

    @Test
    void loadFromFileEpicWithoutSubtasks() {
        taskManager.createEpic(new Epic("Epic #2", "DT"));

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(somePath.toFile());
        assertEquals(0, newTaskManager.getEpicById(1).getSubTasks().size(), "Should contain an empty List");
    }

    @Test
    void loadFromFileWithEmptyHistory() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        taskManager.createTask(new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9)));
        taskManager.createEpic(new Epic("Epic #2", "DT"));
        taskManager.createSubTask(new SubTask("SubTask #3", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(9)));

        TaskManager newTaskManager = FileBackedTasksManager.loadFromFile(somePath.toFile());
        assertEquals(0, newTaskManager.getHistory().size(), "Should contain an empty history List");
    }
}