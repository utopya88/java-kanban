package test;

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
    void addTest() {
        Task task = new Task(1, "task1", "task", Status.NEW);
        Epic epic = new Epic(1, "EPIC1" , "afs");
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
        historyManager.add(new Epic(1, "EPIC1" , "afs"));
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