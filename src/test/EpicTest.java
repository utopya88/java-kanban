package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import service.TaskManager;
import service.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void epicHasNewStatusWhenAllSubtaskAreNew() {
        Epic epic = taskManager.createEpic(new Epic(1,"epic 1", "DE"));
        Subtask subtask1 = taskManager.createSubTask(new Subtask("sub1" , "Sub", Status.NEW, 1));
        Subtask subtask2 = taskManager.createSubTask(new Subtask("sub2" , "Sub", Status.NEW, 1));
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void EpicHasNewStatusWhenIsEmpty() {
        Epic epic = taskManager.createEpic(new Epic("epic1", "de"));
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "статус не корректен");
    }
    @Test
    public void EpicNotAddInSelf() {
        
    }
}