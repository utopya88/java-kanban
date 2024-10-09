package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1,0 );
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        SubTask subTask3 = new SubTask(3, "SubTask #3", "DT", Status.IN_PROGRESS, 2, startTime2, Duration.ofMinutes(9));
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        List<Task> list = historyManager.getHistory();
        assertEquals(3, list.size(), "List size should be 3");
        assertEquals(task1, list.get(0), "Task 1 should be first");
        assertEquals(epic2, list.get(1), "Epic 2 should be second");
        assertEquals(subTask3, list.get(2), "SubTask 3 should be third");
    }

    @Test
    void addSomeTasksDoubleTime() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        SubTask subTask3 = new SubTask(3, "SubTask #3", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(9));
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        historyManager.add(epic2);
        historyManager.add(task1);
        List<Task> list = historyManager.getHistory();
        assertEquals(3, list.size(), "List size should be 3");
        assertEquals(subTask3, list.get(0), "SubTask 3 should be first");
        assertEquals(epic2, list.get(1), "Epic 2 should be second");
        assertEquals(task1, list.get(2), "Task 1 should be third");
    }

    @Test
    void removeFirstElement() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        SubTask subTask3 = new SubTask(3, "SubTask #3", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(9));
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        historyManager.remove(1);
        List<Task> list = historyManager.getHistory();
        assertEquals(2, list.size(), "Size should be 2");
        assertEquals(epic2, list.get(0), "Epic 2 should be element with index 0");
        assertEquals(subTask3, list.get(1), "Sub task 3 should be element with index 1");
    }

    @Test
    void removeLastElement() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        SubTask subTask3 = new SubTask(3, "SubTask #3", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(9));
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        historyManager.remove(3);
        List<Task> list = historyManager.getHistory();
        assertEquals(2, list.size(), "Size should be 2");
        assertEquals(task1, list.get(0), "Task 1 should be first");
        assertEquals(epic2, list.get(1), "Epic 2 should be second");
    }

    @Test
    void removeMiddleElement() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        SubTask subTask3 = new SubTask(3, "SubTask #3", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(9));
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        historyManager.remove(2);
        List<Task> list = historyManager.getHistory();
        assertEquals(2, list.size(), "Size should be 2");
        assertEquals(task1, list.get(0), "Task 1 should be first");
        assertEquals(subTask3, list.get(1), "SubTask 3 should be third");
    }

    @Test
    void getHistory() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        SubTask subTask3 = new SubTask(3, "SubTask #3", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(9));
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        List<Task> list = historyManager.getHistory();
        assertEquals(3, list.size(), "List size should be 3");
    }

    @Test
    void historyIsEmpty() {
        assertEquals(0, historyManager.getHistory().size(), "Should return list with 0 size");
    }
}