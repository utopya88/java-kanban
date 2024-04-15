package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subTasks;
    protected InMemoryHistoryManager historyManager;
    protected int seq = 0;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();

    }

    private int generateId() {
        return ++seq;
    }


    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Task createTask(Task task) {
        if (task.getId() == 0){
            task.setId(generateId());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(generateId());
        }
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubTask(Subtask subTask) {
        int epicId = subTask.getEpic();
        if (subTask.getId() == 0) {
            subTask.setId(generateId());
        }
        subTasks.put(subTask.getId(), subTask);
        if (epics.containsKey(epics.get(epicId))) {
            Epic epic = epics.get(epicId);
            epic.addSubtaskId(subTask.getId());
            epic.setStatus(calculateStatus(epic));
        } else {
            System.out.println("Такого эпика нет");
        }
        return subTask;
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) == null) {
            System.out.println("Таска не найдена");
            return null;
        } else {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) == null) {
            System.out.println("Эпик не найден");
            return null;
        } else {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
    }

    @Override
    public Subtask getSubTaskById(int id) {
        if (subTasks.get(id) == null) {
            System.out.println("Сабтаска не найдена");
            return null;
        } else {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Такого ключа нет");
        }
    }
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epics.get(epic.getId()))) {
            Epic saved = epics.get(epic.getId());
            saved.setName(epic.getName());
            saved.setDescription(epic.getDescription());
        } else {
            System.out.println("Такого ключа нет");
        }

    }
    @Override
    public void updateSubtask(Subtask subTask) {
        int epicId = subTask.getEpic();
        Subtask saved = subTasks.get(subTask.getId());
        if (subTask.getStatus() == null) {
            subTask.setStatus(Status.NEW);
        }
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epicId);
        saved.setEpic(subTask.getEpic());
        epic.setStatus(calculateStatus(epic));
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic saved = epics.get(id);
        for (Integer subTaskIdForDelete : saved.getSubTasksId()) {
            subTasks.remove(subTaskIdForDelete);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("No such id in subTasks storage");
            return;
        }
        Subtask subTask = subTasks.get(id);
        int savedEpicId = subTask.getEpic();
        Epic savedEpic = epics.get(savedEpicId);
        subTasks.remove(id);
        savedEpic.clearSubtaskForId(id);
        savedEpic.setStatus(calculateStatus(savedEpic));
    }
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }
    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }
    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            epic.setStatus(calculateStatus(epic));
        }
    }
    @Override
    public List<Subtask> getSubTaskList(Epic epic) {

        List<Subtask> list = new ArrayList<>();
        for (Integer subTaskId : epic.getSubTasksId()) {
            list.add(subTasks.get(subTaskId));
        }
        return list;
    }

    private Status calculateStatus(Epic epic) {
        List<Integer> subTaskList = epic.getSubTasksId();
        if (subTaskList.isEmpty()) {
            return Status.NEW;
        }
        int newStatus = 0;
        int doneStatus = 0;
        for (Integer subTaskId : subTaskList) {
            if (subTasks.get(subTaskId).getStatus().equals(Status.NEW)) {
                newStatus++;
            }
            if (subTasks.get(subTaskId).getStatus().equals(Status.DONE)) {
                doneStatus++;
            }
        }
        if (newStatus == subTaskList.size()) {
            return Status.NEW;
        }
        if (doneStatus == subTaskList.size()) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}