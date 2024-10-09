package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.Status.*;

public class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    public void epicHasNewStatusWhenSubTaskListIsEmpty() {
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        Assertions.assertEquals(NEW, epic.getStatus(), "Status generates incorrectly");
    }

    @Test
    public void epicHasNewStatusWhenAllSubTasksAreNew() {
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("SubTask #9", "DS", Status.NEW, 1, LocalDateTime.now(), Duration.ofDays(2)));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("SubTask #10", "DS", Status.NEW, 1, LocalDateTime.of(2023, 4, 30, 21, 30), Duration.ofMinutes(35)));
        Assertions.assertEquals(NEW, epic.getStatus(), "Status calculates incorrectly");
    }

    @Test
    public void epicHasDoneStatusWhenAllSubTasksAreDone() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        taskManager.createSubTask(new SubTask("SubTask #9", "DS", Status.NEW, 1, startTime1, Duration.ofMinutes(9)));
        taskManager.createSubTask(new SubTask("SubTask #10", "DS", Status.NEW, 1, startTime2, Duration.ofMinutes(15)));
        taskManager.updateSubTask(new SubTask(2, "SubTask #9", "DS", DONE, 1, startTime1, Duration.ofMinutes(9)));
        taskManager.updateSubTask(new SubTask(3, "SubTask #10", "DS", DONE, 1, startTime2, Duration.ofMinutes(15)));
        Assertions.assertEquals(DONE, epic.getStatus(), "Status calculates incorrectly");
    }

    @Test
    public void epicHasInProgressStatusWhenSubTasksAreDoneAndNew() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        taskManager.createSubTask(new SubTask("SubTask #9", "DS", Status.NEW, 1, startTime1, Duration.ofMinutes(9)));
        taskManager.createSubTask(new SubTask("SubTask #10", "DS", Status.NEW, 1, startTime2, Duration.ofMinutes(15)));
        taskManager.updateSubTask(new SubTask(2, "SubTask #9", "DS", DONE, 1, startTime1, Duration.ofMinutes(9)));
        taskManager.updateSubTask(new SubTask(3, "SubTask #10", "DS", NEW, 1, startTime2, Duration.ofMinutes(15)));
        Assertions.assertEquals(IN_PROGRESS, epic.getStatus(), "Status calculates incorrectly");
    }

    @Test
    public void epicHasInProgressStatusWhenSubTasksAreInProgress() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Epic epic = taskManager.createEpic(new Epic("Epic #4", "DE"));
        taskManager.createSubTask(new SubTask("SubTask #9", "DS", Status.NEW, 1, startTime1, Duration.ofMinutes(9)));
        taskManager.createSubTask(new SubTask("SubTask #10", "DS", Status.NEW, 1, startTime2, Duration.ofMinutes(15)));
        taskManager.updateSubTask(new SubTask(2, "SubTask #9", "DS", IN_PROGRESS, 1, startTime1, Duration.ofMinutes(9)));
        taskManager.updateSubTask(new SubTask(3, "SubTask #10", "DS", IN_PROGRESS, 1, startTime2, Duration.ofMinutes(15)));
        Assertions.assertEquals(IN_PROGRESS, epic.getStatus(), "Status calculates incorrectly");
    }
}