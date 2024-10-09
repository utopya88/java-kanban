package test;

import model.Epic;
import model.SubTask;
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
        SubTask subtask1 = taskManager.createSubTask(new SubTask("sub1", "Sub", Status.NEW, 1));
        SubTask subtask2 = taskManager.createSubTask(new SubTask("sub2", "Sub", Status.NEW, 1));
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }
}