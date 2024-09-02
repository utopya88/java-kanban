package test;
import java.util.List;
import model.Epic;
import model.Subtask;
import model.Task;
import service.HistoryManager;
import service.Managers;
import model.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;



class HistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void removeFirstElement() {
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW);
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        Subtask subTask3 = new Subtask(3, "SubTask #3", Status.NEW,1);
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        historyManager.remove(1);
        List<Task> list = historyManager.getHistory();
        Assertions.assertEquals(2, list.size(), "Size should be 2");
        Assertions.assertEquals(epic2, list.get(0), "Epic 2 should be element with index 0");
        Assertions.assertEquals(subTask3, list.get(1), "Sub task 3 should be element with index 1");
    }

    @Test
    void removeLastElement() {
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW);
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        Subtask subTask3 = new Subtask(3, "SubTask #3", "DT", Status.NEW, 2);
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        historyManager.remove(3);
        List<Task> list = historyManager.getHistory();
        Assertions.assertEquals(2, list.size(), "Size should be 2");
        Assertions.assertEquals(task1, list.get(0), "Task 1 should be first");
        Assertions.assertEquals(epic2, list.get(1), "Epic 2 should be second");
    }

    @Test
    void removeMiddleElement() {
        Task task1 = new Task(1, "Task #1", "DT", Status.NEW);
        Epic epic2 = new Epic(2, "Epic #2", "DT");
        Subtask subTask3 = new Subtask(3, "SubTask #3", "DT", Status.NEW, 2);
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subTask3);
        historyManager.remove(2);
        List<Task> list = historyManager.getHistory();
        Assertions.assertEquals(2, list.size(), "Size should be 2");
        Assertions.assertEquals(task1, list.get(0), "Task 1 should be first");
        Assertions.assertEquals(subTask3, list.get(1), "SubTask 3 should be third");
    }

    @Test
    void addTest() {
        Task task = new Task(1, "task1", "task", Status.NEW);
        Epic epic = new Epic(1, "EPIC1", "afs");
        Subtask subtask = new Subtask("fas", "afs", Status.IN_PROGRESS, 1);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        Assertions.assertEquals(task, historyManager.getHistory().get(0), "таска не на своем месте");
        Assertions.assertEquals(epic, historyManager.getHistory().get(1), "эпик не на своем месте");
        Assertions.assertEquals(subtask, historyManager.getHistory().get(2), "сабтаск не на своем месте");
    }

    @Test
    void getHistoryTest() {
        historyManager.add(new Subtask("fas", "afs", Status.IN_PROGRESS, 1));
        historyManager.add(new Epic(1, "EPIC1", "afs"));
        historyManager.add(new Subtask("fas", "afs", Status.IN_PROGRESS, 1));
        Assertions.assertEquals(3, historyManager.getHistory().size(), "Размер не совпадает");
    }

    @Test
    void historyIsEmpty() {
        Assertions.assertEquals(0, historyManager.getHistory().size(), "список должен быть пустым");
    }

    @Test
    void updateHistotyTask() {
        Task task = new Task(1,"task1", "task1");
        historyManager.add(task);
        task.setName("task2");
        task.setDescription("task2");
        task.setId(2);
        historyManager.add(task);
        Assertions.assertEquals(new Task(1,"task1", "task1"), historyManager.getHistory().get(0),
                "История не хранит предыдущую версию");
        Assertions.assertEquals(new Task(2,"task2", "task2"), historyManager.getHistory().get(1),
                "История не хранит обновленную версию");
    }

}