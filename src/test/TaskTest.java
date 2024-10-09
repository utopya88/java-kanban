package test;

import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.SubTask;
import model.Task;
import model.Status;
import service.TaskManager;
import service.Managers;
import service.InMemoryTaskManager;
import service.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



class TaskTest {
    TaskManager taskManager;
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    void getAllTask() {
        Task task1 = new Task("Task1", "sss");
        Task task2 = new Task("Task2", "sss");
        Assertions.assertEquals(2, taskManager.getAllTasks().size());
        Assertions.assertEquals(task1, taskManager.getAllTasks().get(0), "первый элемент должен быть 1 в списке");
        Assertions.assertEquals(task2, taskManager.getAllTasks().get(1), "второй элемент должен быть 2 в списке");
    }

    @Test
    void equalityTaskById() {
        Task task1 = new Task(1, "task1", "dd");
        Task task2 = new Task(1,"task2", "dd");
        Assertions.assertEquals(task1,task2, "Таски с одинаковыми айди должны быть равны");
    }

    @Test
    void equalityEpicById() {
        Epic epic1 = new Epic(1, "epic1", "dd");
        Epic epic2 = new Epic(1,"epic2", "dd");
        assertEquals(epic1,epic2, "Эпики с одинаковыми айди должны быть равны");
    }

    @Test
    void equalitySubtaskById() {
        SubTask subtask1 = new SubTask(1, "Subtask1", "dd", Status.NEW, 1);
        SubTask subtask2 = new SubTask(1,"Subtask2", "dd", Status.NEW, 1);
        assertEquals(subtask1,subtask2, "Эпики с одинаковыми айди должны быть равны");
    }

    @Test
    void inMemoryTaskIdSearchTaskById() {
        Task task = new Task("task1", "task1");
        inMemoryTaskManager.createTask(task);
        Assertions.assertEquals(task, inMemoryTaskManager.getTaskById(1), "Таска не находится по айди");

    }

    @Test
    void inMemoryTaskIdSearchEpicById() {
        Epic epic = new Epic("epic1", "epic1");
        inMemoryTaskManager.createEpic(epic);
        Assertions.assertEquals(epic, inMemoryTaskManager.getEpicById(1), "Эпик не находится по айди");

    }

    @Test
    void inMemoryTaskIdSearchSubtaskById() {
        SubTask subtask = new SubTask("Subtask1", "subtask1", Status.NEW,1);
        inMemoryTaskManager.createSubTask(subtask);
        Assertions.assertEquals(subtask, inMemoryTaskManager.getSubTaskById(1), "Сабтаск не находится по айди");

    }

    @Test
    void immutabilityFieldsTask() {
        Task task = new Task("task", "taskD");
        inMemoryTaskManager.createTask(task);
        Assertions.assertEquals("task", inMemoryTaskManager.getTaskById(1).getName(), "имен" +
                "а не" +
                "совпадают");
        Assertions.assertEquals("taskD", inMemoryTaskManager.getTaskById(1).getDescription(), "Описа" +
                "ния " +
                "не совпадают");
        Assertions.assertEquals(Status.NEW, inMemoryTaskManager.getTaskById(1).getStatus(),"статус автомати" +
                "чески не генерируется");
    }

    @Test
    void immutabilityFieldsEpic() {
        Epic epic = new Epic("epic", "epicD");
        inMemoryTaskManager.createTask(epic);
        Assertions.assertEquals("epic", inMemoryTaskManager.getEpicById(1).getName(), "имена не" +
                "совпадают");
        Assertions.assertEquals("epicD", inMemoryTaskManager.getEpicById(1).getDescription(), "Описания " +
                "не совпадают");
    }

    @Test
    void immutabilityFieldsSubtask() {
        SubTask subtask = new SubTask(1, "subtask", "SubtaskD", Status.NEW,1);
        inMemoryTaskManager.createSubTask(subtask);
        Assertions.assertEquals("subtask", inMemoryTaskManager.getSubTaskById(1).getName(), "имена не" +
                "совпадают");
        Assertions.assertEquals("SubtaskD", inMemoryTaskManager.getSubTaskById(1).getDescription(), "Описания " +
                "не совпадают");
        Assertions.assertEquals(1,inMemoryTaskManager.getSubTaskById(1).getId(), "айди не совпадает");
        Assertions.assertEquals(Status.NEW, inMemoryTaskManager.getSubTaskById(1).getStatus(), "статус не" +
                "совпадают");
    }
}