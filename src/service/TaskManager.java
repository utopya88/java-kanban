package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    List<Task> getPrioritizedTasks();

    List<SubTask> getSubTaskList(Epic epic);

    List<Task> getHistory();
}
