package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    abstract void init();

    @Test
    void getAllTasks() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Task task1 = new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Task task2 = new Task("New Task", "New DT", Status.NEW, startTime2, Duration.ofMinutes(5));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> taskList = taskManager.getAllTasks();
        assertEquals(2, taskList.size(), "Should contain 2 elements");
        assertEquals(task1, taskList.get(0), "First task should be first in list");
        assertEquals(task2, taskList.get(1), "Second task should be second in list");
    }

    @Test
    void getAllTasksFromEmptyList() {
        List<Task> taskList = taskManager.getAllTasks();
        assertEquals(0, taskList.size(), "Should contain 0 elements");
    }

    @Test
    void getAllEpics() {
        Epic epic1 = new Epic("Epic #1", "DT");
        Epic epic2 = new Epic("Epic #2", "DT");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        List<Epic> epicList = taskManager.getAllEpics();
        assertEquals(2, epicList.size(), "Should contain 2 elements");
        assertEquals(epic1, epicList.get(0), "First epic should be first in list");
        assertEquals(epic2, epicList.get(1), "Second epic should be second in list");
    }

    @Test
    void getAllEpicsFromEmptyList() {
        List<Epic> epicList = taskManager.getAllEpics();
        assertEquals(0, epicList.size(), "Should contain 0 elements");
    }

    @Test
    void getAllSubTasks() {
        taskManager.createEpic(new Epic("Epic #1", "DT"));
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask1 = new SubTask("Subtask #1", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask2 = new SubTask("Subtask #1", "DT", Status.NEW, 1, startTime2, Duration.ofMinutes(5));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        List<SubTask> subTaskList = taskManager.getAllSubTasks();
        assertEquals(2, subTaskList.size(), "Should contain 2 elements");
        assertEquals(subTask1, subTaskList.get(0), "First sub task should be first in list");
        assertEquals(subTask2, subTaskList.get(1), "Second sub task should be second in list");
    }

    @Test
    void getAllSubTasksFromEmptyList() {
        List<SubTask> subTaskList = taskManager.getAllSubTasks();
        assertEquals(0, subTaskList.size(), "Should contain 0 elements");
    }

    @Test
    void createTask() {
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        Task task = new Task("Task #1", "DT", Status.NEW, startTime, Duration.ofMinutes(9));
        Task result = taskManager.createTask(task);
        assertNotNull(result, "Task create failure");
        assertTrue(result.getId() > 0, "ID counts failure");
        assertEquals("Task #1", result.getName(), "Wrong name was written");
        assertEquals("DT", result.getDescription(), "Wrong description was written");
        assertEquals(startTime, result.getStartTime(), "Wrong start time was written");
        assertEquals(Duration.ofMinutes(9), result.getDuration(), "Wrong duration was written");
        assertEquals(startTime.plus(Duration.ofMinutes(9)), result.getEndTime(), "Wrong end time calculation");
    }

    @Test
    void createTaskWithNull() {
        Task result = taskManager.createTask(null);
        assertNull(result, "If incoming is null, result is null");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Epic #1", "DT");
        Epic result = taskManager.createEpic(epic);
        assertNotNull(result, "Epic create failure");
        assertTrue(result.getId() > 0, "ID counts failure");
        assertEquals("Epic #1", result.getName(), "Wrong name was written");
        assertEquals("DT", result.getDescription(), "Wrong description was written");
    }

    @Test
    void createEpicWithNull() {
        Epic result = taskManager.createEpic(null);
        assertNull(result, "If incoming is null, result should be null");
    }

    @Test
    void createSubTaskWhenEpicExists() {
        taskManager.createEpic(new Epic("Epic #1", "DT"));
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        SubTask subTask = new SubTask("Subtask #1", "DT", Status.NEW, 1, startTime, Duration.ofMinutes(9));
        SubTask result = taskManager.createSubTask(subTask);
        assertNotNull(result, "Subtask create failure");
        assertTrue(result.getId() > 0, "ID counts failure");
        assertEquals("Subtask #1", result.getName(), "Wrong name was written");
        assertEquals("DT", result.getDescription(), "Wrong description was written");
        assertEquals(1, result.getEpic(), "Epic number was written incorrectly");
        assertEquals(startTime, result.getStartTime(), "Wrong start time was written");
        assertEquals(Duration.ofMinutes(9), result.getDuration(), "Wrong duration was written");
        assertEquals(startTime.plus(Duration.ofMinutes(9)), result.getEndTime(), "Wrong end time calculation");
    }

    @Test
    void createSubTaskWithNull() {
        SubTask result = taskManager.createSubTask(null);
        assertNull(result, "If incoming is null, result is null");
    }

    @Test
    void epicStartTimeDurationAndEndTimeShouldChangeWithSubTasks() {
        Epic epic = taskManager.createEpic(new Epic("Epic #1", "DT"));
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask2 = new SubTask("Subtask #2", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask3 = new SubTask("Subtask #3", "DT", Status.NEW, 1, startTime2, Duration.ofMinutes(5));
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        assertEquals(startTime1, epic.getStartTime(), "Epic should starts with first sub task start");
        assertEquals(startTime2.plus(Duration.ofMinutes(5)), epic.getEndTime(), "Epic should ends with last sub task end");
        assertEquals(Duration.ofMinutes(14), epic.getDuration(), "Duration of Epic should be from start of first subtask till end of las sub task");
    }

    @Test
    void epicSubTaskListShouldChangeWithSubtasks() {
        Epic epic = taskManager.createEpic(new Epic("Epic #1", "DT"));
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask2 = new SubTask("Subtask #2", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask3 = new SubTask("Subtask #3", "DT", Status.NEW, 1, startTime2, Duration.ofMinutes(5));
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        assertEquals(2, epic.getSubTasks().size(), "Some Subtasks doesn't write in epic list");
        taskManager.deleteSubTaskById(3);
        assertEquals(1, epic.getSubTasks().size(), "Some Subtasks doesn't delete from epic list");
    }

    @Test
    void createSubTaskWhenNoEpic() {
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        SubTask subTask = new SubTask("Subtask #1", "DT", Status.NEW, 1, startTime, Duration.ofMinutes(9));
        SubTask result = taskManager.createSubTask(subTask);
        assertNull(result, "Subtask without epic should return null");
    }

    @Test
    void getTaskById() {
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        Task task = new Task("Task #1", "DT", Status.NEW, startTime, Duration.ofMinutes(9));
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(1), "Index in storage is incorrect");
        assertNull(taskManager.getTaskById(0), "No such task index in storage - result should be Null");
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("Epic #1", "DT");
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpicById(1), "Index in storage is incorrect");
        assertNull(taskManager.getEpicById(0), "No such task index in storage - result should be Null");
    }

    @Test
    void getSubTaskById() {
        taskManager.createEpic(new Epic("Epic #1", "DT"));
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        SubTask subTask = new SubTask("Subtask #1", "DT", Status.NEW, 1, startTime, Duration.ofMinutes(9));
        taskManager.createSubTask(subTask);
        assertEquals(subTask, taskManager.getSubTaskById(2), "Index in storage is incorrect");
        assertNull(taskManager.getSubTaskById(3), "No such task index in storage - result should be Null");
    }

    @Test
    void updateTaskWithIncomingTaskWithNewStatusAndNewTimeAndNewDuration() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Task task1 = new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Task task2 = new Task(1, "New Task", "New DT", Status.IN_PROGRESS, startTime2, Duration.ofMinutes(5));
        taskManager.createTask(task1);
        taskManager.updateTask(task2);
        assertEquals("New Task", taskManager.getTaskById(1).getName(), "Task name changed incorrectly");
        assertEquals("New DT", taskManager.getTaskById(1).getDescription(), "Task description changed incorrectly");
        assertEquals(startTime2, taskManager.getTaskById(1).getStartTime(), "Task start time changed incorrectly");
        assertEquals(Duration.ofMinutes(5), taskManager.getTaskById(1).getDuration(), "Task duration changed incorrectly");
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(1).getStatus(), "Task status changed incorrectly");
    }

    @Test
    void updateTaskInEmptyHashMap() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        Task task = new Task(1, "Task #1", "DT", Status.IN_PROGRESS, startTime1, Duration.ofMinutes(9));
        taskManager.updateTask(task);
        assertEquals(0, taskManager.getAllTasks().size(), "Should be no changes in storage");
    }

    @Test
    void updateTaskWithIncomingTaskWithNullStatus() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Task task1 = new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Task task2 = new Task(1, "New Task", "New DT", null, startTime2, Duration.ofMinutes(5));
        taskManager.createTask(task1);
        taskManager.updateTask(task2);
        assertEquals(Status.NEW, taskManager.getTaskById(1).getStatus(), "Task status changed incorrectly");
    }

    @Test
    void updateTaskWithNull() {
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        Task task = new Task("Task #1", "DT", Status.NEW, startTime, Duration.ofMinutes(9));
        taskManager.createTask(task);
        taskManager.updateTask(null);
        assertEquals(task, taskManager.getTaskById(1), "If incoming is Null, task shouldn't change");
    }

    @Test
    void updateTaskWithWrongId() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Task task1 = new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Task task2 = new Task(2, "New Task", "New DT", Status.IN_PROGRESS, startTime2, Duration.ofMinutes(5));
        taskManager.createTask(task1);
        taskManager.updateTask(task2);
        assertEquals(task1, taskManager.getTaskById(1), "Task should not change");
        assertNull(taskManager.getTaskById(2), "Second task should not be written");
    }

    @Test
    void updateEpicWithIncomingEpic() {
        Epic epic1 = new Epic("Epic #1", "DT");
        Epic epic2 = new Epic(1, "New Epic", "New DT");
        taskManager.createEpic(epic1);
        taskManager.updateEpic(epic2);
        assertEquals("New Epic", taskManager.getEpicById(1).getName(), "Task name changed incorrectly");
        assertEquals("New DT", taskManager.getEpicById(1).getDescription(), "Task description changed incorrectly");
        assertNull(taskManager.getEpicById(1).getStartTime(), "The start time of new epic should ne null");
        assertNull(taskManager.getEpicById(1).getDuration(), "Task duration of epic should be null");
        assertNull(taskManager.getEpicById(1).getEndTime(), "Task duration of epic should be null");
    }

    @Test
    void updateEpicInEmptyHashMap() {
        Epic epic = new Epic(1, "Epic #1", "DT");
        taskManager.updateEpic(epic);
        assertEquals(0, taskManager.getAllEpics().size(), "Should be no changes in storage");
    }

    @Test
    void updateEpicWithNullEpic() {
        Epic epic = new Epic("Epic #1", "DT");
        taskManager.createEpic(epic);
        taskManager.updateEpic(null);
        assertEquals(epic, taskManager.getEpicById(1), "If incoming is Null, epic shouldn't change");
    }

    @Test
    void updateEpicWithWrongID() {
        Epic epic1 = new Epic("Epic #1", "DT");
        Epic epic2 = new Epic(2, "New Epic", "New DT");
        taskManager.createEpic(epic1);
        taskManager.updateEpic(epic2);
        assertEquals(epic1, taskManager.getEpicById(1), "Epic should not change");
        assertNull(taskManager.getEpicById(2), "Second epic should not be written");
    }

    @Test
    void updateSubTaskWithIncomingTaskWithCorrectEpicWithNewTimeAndDuration() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Epic epic1 = new Epic("Epic #1", "DT");
        SubTask subTask2 = new SubTask("SubTask #2", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask3 = new SubTask(2, "New SubTask", "New DT", Status.IN_PROGRESS, 1, startTime2, Duration.ofMinutes(5));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(subTask3);
        assertEquals("New SubTask", taskManager.getSubTaskById(2).getName(), "Sub task name changed incorrectly");
        assertEquals("New DT", taskManager.getSubTaskById(2).getDescription(), "Sub task description changed incorrectly");
        assertEquals(startTime2, taskManager.getSubTaskById(2).getStartTime(), "Sub task start time changed incorrectly");
        assertEquals(Duration.ofMinutes(5), taskManager.getSubTaskById(2).getDuration(), "Sub task duration changed incorrectly");
        assertEquals(Status.IN_PROGRESS, taskManager.getSubTaskById(2).getStatus(), "Sub task status changed incorrectly");
    }

    @Test
    void updateSubTaskInEmptyHashMap() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        SubTask subTask = new SubTask(1, "SubTask #1", "DT", Status.IN_PROGRESS, 1, startTime1, Duration.ofMinutes(9));
        taskManager.updateTask(subTask);
        assertEquals(0, taskManager.getAllSubTasks().size(), "Should be no changes in storage");
    }

    @Test
    void updateSubTaskWithNull() {
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        Epic epic1 = new Epic("Epic #1", "DT");
        SubTask subTask2 = new SubTask("SubTask #2", "DT", Status.NEW, 1, startTime, Duration.ofMinutes(9));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(null);
        assertEquals(subTask2, taskManager.getSubTaskById(2), "If incoming is Null, sub task shouldn't change");
    }

    @Test
    void updateSubTaskWithWrongId() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Epic epic1 = new Epic("Epic #1", "DT");
        SubTask subTask2 = new SubTask("SubTask #2", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask3 = new SubTask(3, "New SubTask", "New DT", Status.IN_PROGRESS, 1, startTime2, Duration.ofMinutes(5));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask2);
        taskManager.updateSubTask(subTask3);
        assertEquals(subTask2, taskManager.getSubTaskById(2), "Sub task should not change");
        assertNull(taskManager.getSubTaskById(3), "Second sub task should not be written");
    }

    @Test
    void deleteTaskById() {
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        Task task1 = new Task("Task #1", "DT", Status.NEW, startTime, Duration.ofMinutes(9));
        taskManager.createTask(task1);
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1), "Should be null after delete");
    }

    @Test
    void deleteTaskByIdFromEmptyMap() {
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1), "Should be null after delete");
        assertEquals(0, taskManager.getAllTasks().size(), "Storage should stay empty");
    }

    @Test
    void deleteTaskByWrongId() {
        LocalDateTime startTime = LocalDateTime.of(2023, 5, 8, 1, 0);
        Task task1 = new Task("Task #1", "DT", Status.NEW, startTime, Duration.ofMinutes(9));
        taskManager.createTask(task1);
        taskManager.deleteTaskById(2);
        assertNull(taskManager.getTaskById(2), "Should be null if no index to delete");
        assertEquals(task1, taskManager.getTaskById(1), "Should be no changes with task");
    }

    @Test
    void deleteEpicById() {
        Epic epic1 = new Epic("Epic #1", "DT");
        taskManager.createEpic(epic1);
        taskManager.deleteEpicById(1);
        assertNull(taskManager.getEpicById(1), "Should be null after delete");
    }

    @Test
    void deleteEpicByIdFromEmptyMap() {
        taskManager.deleteEpicById(1);
        assertNull(taskManager.getEpicById(1), "Should be null after delete");
        assertEquals(0, taskManager.getAllEpics().size(), "Storage should stay empty");
    }

    @Test
    void deleteEpicWithWrongId() {
        Epic epic1 = new Epic("Epic #1", "DT");
        taskManager.createEpic(epic1);
        taskManager.deleteEpicById(2);
        assertNull(taskManager.getEpicById(2), "Should be null if ID doesn't exist");
        assertEquals(epic1, taskManager.getEpicById(1), "Should be no changes with epic");
    }

    @Test
    void deleteSubTaskById() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        Epic epic1 = new Epic("Epic #1", "DT");
        SubTask subTask2 = new SubTask("SubTask #2", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask2);
        taskManager.deleteSubTaskById(2);
        assertNull(taskManager.getSubTaskById(2), "Sub task was not deleted");
        assertEquals(0, taskManager.getEpicById(1).getSubTasks().size(), "Epic sub task list should change");
    }

    @Test
    void deleteSubTaskByIdFromEmptyMap() {
        taskManager.deleteSubTaskById(1);
        assertNull(taskManager.getSubTaskById(1), "Should be null after delete");
        assertEquals(0, taskManager.getAllSubTasks().size(), "Storage should stay empty");
    }

    @Test
    void deleteSubTaskWithWrongId() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        Epic epic1 = new Epic("Epic #1", "DT");
        SubTask subTask2 = new SubTask("SubTask #2", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask2);
        taskManager.deleteSubTaskById(3);
        assertNull(taskManager.getSubTaskById(3), "Should be null if ID doesn't exist");
        assertEquals(subTask2, taskManager.getSubTaskById(2), "Should be no changes with sub task");
    }

    @Test
    void deleteAllTasks() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Task task1 = new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9));
        Task task2 = new Task("New Task", "New DT", Status.NEW, startTime2, Duration.ofMinutes(5));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();
        assertNull(taskManager.getTaskById(1), "Should be deleted");
        assertNull(taskManager.getTaskById(1), "Should be deleted");
        assertEquals(0, taskManager.getAllTasks().size(), "List of tasks should be 0 size");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic("Epic #1", "DT");
        Epic epic2 = new Epic("Epic #2", "New DT");
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask3 = new SubTask("SubTask #3", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask4 = new SubTask("SubTask #4", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(5));
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask3);
        taskManager.createSubTask(subTask4);
        taskManager.deleteAllEpics();
        assertNull(taskManager.getEpicById(1), "Should be deleted");
        assertNull(taskManager.getEpicById(2), "Should be deleted");
        assertEquals(0, taskManager.getAllEpics().size(), "List of epics should be 0 size");
        assertNull(taskManager.getSubTaskById(3), "Should be deleted");
        assertNull(taskManager.getSubTaskById(4), "Should be deleted");
        assertEquals(0, taskManager.getAllSubTasks().size(), "List of subtasks should be 0 size");
    }

    @Test
    void deleteAllSubTasks() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask1 = new SubTask("SubTask #3", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask2 = new SubTask("SubTask #4", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(5));
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.deleteAllEpics();
        assertNull(taskManager.getSubTaskById(1), "Should be deleted");
        assertNull(taskManager.getSubTaskById(2), "Should be deleted");
        assertEquals(0, taskManager.getAllSubTasks().size(), "List of subtasks should be 0 size");
    }

    @Test
    void getSubTaskList() {
        Epic epic1 = new Epic("Epic #1", "DT");
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask2 = new SubTask("SubTask #2", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask3 = new SubTask("SubTask #3", "DT", Status.NEW, 1, startTime2, Duration.ofMinutes(5));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        List<SubTask> list = taskManager.getSubTaskList(epic1);
        assertEquals(2, list.size(), "Should contain 2 elements");
    }

    @Test
    void getSubTaskListFromEmptyEpicList() {
        Epic epic1 = new Epic("Epic #1", "DT");
        assertNull(taskManager.getSubTaskList(epic1), "Should return null");
    }

    @Test
    void getSubTaskListFromEmptySubTaskList() {
        Epic epic1 = new Epic("Epic #1", "DT");
        taskManager.createEpic(epic1);
        List<SubTask> list = taskManager.getSubTaskList(epic1);
        assertEquals(0, list.size(), "Should contain 0 elements");
    }

    @Test
    void getSubTaskListFomEpicWithWrongId() {
        Epic epic1 = new Epic("Epic #1", "DT");
        Epic epic4 = new Epic(4, "Epic #4", "DT");
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask2 = new SubTask("SubTask #2", "DT", Status.NEW, 1, startTime1, Duration.ofMinutes(9));
        SubTask subTask3 = new SubTask("SubTask #3", "DT", Status.NEW, 1, startTime2, Duration.ofMinutes(5));
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        assertNull(taskManager.getSubTaskList(epic4), "Should return null");
    }

    @Test
    void getHistory() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Task task1 = taskManager.createTask(new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9)));
        Epic epic2 = taskManager.createEpic(new Epic("Epic #2", "DT"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("SubTask #3", "DT", Status.NEW, 2, startTime2, Duration.ofMinutes(9)));
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubTaskById(3);
        List<Task> list = taskManager.getHistory();
        assertEquals(3, list.size(), "List size should be 3");
        assertEquals(task1, list.get(0), "Task 1 should be first");
        assertEquals(epic2, list.get(1), "Epic 2 should be second");
        assertEquals(subTask3, list.get(2), "SubTask 3 should be third");
        taskManager.getEpicById(2);
        taskManager.getTaskById(1);

        List<Task> list1 = taskManager.getHistory();
        assertEquals(subTask3, list1.get(0), "SubTask 3 should be first");
        assertEquals(epic2, list1.get(1), "Epic 2 should be second");
        assertEquals(task1, list1.get(2), "Task 1 should be third");
        taskManager.deleteTaskById(1);

        List<Task> list2 = taskManager.getHistory();
        assertEquals(2, list2.size(), "History should have size 2 after deletion of 1 task");
    }

    @Test
    void getHistoryWhenFromEmptyList() {
        List<Task> list = taskManager.getHistory();
        assertEquals(0, list.size(), "Should return empty list");
    }

    @Test
    void getPrioritizedTasks() {
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 20);
        Epic epic1 = taskManager.createEpic(new Epic("Epic #1", "DT"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("SubTask #2", "DT", Status.NEW, 1, startTime2, Duration.ofMinutes(9)));
        Task task3 = taskManager.createTask(new Task("Task #3", "DT", Status.NEW, startTime1, Duration.ofMinutes(9)));
        List<Task> list1 = taskManager.getPrioritizedTasks();
        assertEquals(task3, list1.get(0), "Task #3 should be the first element of the list");
        assertEquals(subTask2, list1.get(1), "Sub Task #2 should be the second element of the list");

        LocalDateTime startTime3 = LocalDateTime.of(2023, 5, 8, 0, 20);
        SubTask subTask4 = taskManager.createSubTask(new SubTask("SubTask #4", "DT", Status.NEW, 1, startTime3, Duration.ofMinutes(9)));
        List<Task> list2 = taskManager.getPrioritizedTasks();
        assertEquals(subTask4, list2.get(0), "Sub task #4 should be the fist element of the list");
        assertEquals(task3, list2.get(1), "Task #3 should be the second element of the list");
        assertEquals(subTask2, list2.get(2), "Sub Task #2 should be the third element of the list");
    }
}