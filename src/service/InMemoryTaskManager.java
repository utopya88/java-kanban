package service;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.Duration;

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
    protected Set<Task> prioritizedTasks;
    protected int seq = 0;

    public InMemoryTaskManager(InMemoryHistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(taskComparator);

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
        if (task == null) {
            return null;
        }
        if (!valid(task)) {
            System.out.println("Задача пересекается по времени с имеющимися задачами");
            return null;
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubTask(Subtask subTask) {
        if (subTask == null) {
            return null;
        }
        int saved = subTask.getEpic();
        if (!epics.containsKey(saved)) { //проверка на наличие эпиков к которым относятся саб таски
            System.out.println("No such epic in Hash Map");
            return null;
        }
        if (!valid(subTask)) {
            System.out.println("Подзадача пересекается по времени с имеющимися задачами");
            return null;
        }
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(saved); // вынимаем из мапы эпик, по id эпика, который пришел с сабтаской

        epic.addSubtask(subTask.getId());
        epic.setStatus(calculateStatus(epic)); // расчитываем статус
        createEpicDateTime(epic);
        prioritizedTasks.add(subTask);
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
        if (task == null) {
            System.out.println("The incoming task is null");
            return;
        }
        if (!tasks.containsKey(task.getId())) {
            System.out.println("The task has incorrect number");
            return;
        }
        Task saved = tasks.get(task.getId());
        Task tempTask = tasks.get(task.getId());
        prioritizedTasks.remove(saved);
        if (!valid(task)) {
            System.out.println("Задача пересекается по времени с имеющимися задачами");
            prioritizedTasks.add(tempTask);
            return;
        }
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        tasks.put(task.getId(), task);

        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("The incoming epic is null");
            return;
        }
        if (!epics.containsKey(epic.getId())) {
            System.out.println("The epic has incorrect number");
            return;
        }
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        if (subTask == null) {
            System.out.println("The incoming subtask is null");
            return;
        }
        if (!subTasks.containsKey(subTask.getId())) {
            System.out.println("The subtask has incorrect number");
            return;
        }
        int epicId = subTask.getEpic();
        if (!epics.containsKey(epicId)) {
            System.out.println("The epic for subtask has incorrect number");
            return;
        }
        List<Integer> epicSubTaskList = epics.get(epicId).getSubTasksId();
        if (!epicSubTaskList.contains(subTask.getId())) {
            System.out.println("Неправильно указан эпик в подзадаче");
            return;
        }
        Subtask saved = subTasks.get(subTask.getId());
        Subtask tempSubTask = subTasks.get(subTask.getId());
        prioritizedTasks.remove(saved);
        if (!valid(subTask)) {
            System.out.println("Подзадача пересекается по времени с имеющимися задачами");
            prioritizedTasks.add(tempSubTask);
            return;
        }
        if (subTask.getStatus() == null) {
            subTask.setStatus(Status.NEW);
        }
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epicId);
        saved.setEpic(subTask.getEpic());
        epic.setStatus(calculateStatus(epic));
        createEpicDateTime(epic);
        prioritizedTasks.add(subTask);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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


    private void createEpicDateTime(Epic epic) {
        List<Integer> subTaskList = epic.getSubTasksId();
        if (subTaskList.isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        for (Integer subTaskId : subTaskList) {
            LocalDateTime subTaskStartTime = subTasks.get(subTaskId).getStartTime();
            LocalDateTime subTaskEndTime = subTasks.get(subTaskId).getEndTime();
            Duration subTaskDuration = subTasks.get(subTaskId).getDuration();
            if (epic.getStartTime() == null) {
                epic.setStartTime(subTaskStartTime);
                epic.setEndTime(subTaskEndTime);
                if (epic.getDuration() == null) {
                    epic.setDuration(subTaskDuration);
                } else {
                    epic.setDuration(epic.getDuration().plus(subTaskDuration));
                }
            } else {
                if (epic.getStartTime().isAfter(subTaskStartTime)) {
                    epic.setStartTime(subTaskStartTime);
                    if (epic.getDuration() == null) {
                        epic.setEndTime(subTaskEndTime);
                    } else {
                        epic.setDuration(epic.getDuration().plus(subTaskDuration));
                    }
                }
                if (subTaskEndTime.isAfter(epic.getEndTime())) {
                    epic.setEndTime(subTaskEndTime);
                    if (epic.getDuration() == null) {
                        epic.setEndTime(subTaskEndTime);
                    } else {
                        epic.setDuration(epic.getDuration().plus(subTaskDuration));
                    }
                }
            }
        }
    }

    private boolean valid(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return true;
        }
        LocalDateTime start = task.getStartTime();
        LocalDateTime finish = task.getEndTime();
        if (start == null) {
            return true;
        }
        for (Task prioritizedTask : prioritizedTasks) {
            LocalDateTime begin = prioritizedTask.getStartTime();
            LocalDateTime end = prioritizedTask.getEndTime();

            if (start.isEqual(begin) || start.isEqual(end) || finish.isEqual(end) || finish.isEqual(begin)) {
                return false;
            }
            if ((start.isAfter(begin) && start.isBefore(end)) || (finish.isAfter(begin) && finish.isBefore(end))) {
                return false;
            }
            if (start.isBefore(begin) && finish.isAfter(end)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    Comparator<Task> taskComparator = (o1, o2) -> {
        if (o1.getId() == o2.getId()) {
            return 0;
        }
        if (o1.getStartTime() == null) {
            return 1;
        }
        if (o2.getStartTime() == null) {
            return -1;
        }
        if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else {
            return o1.getId() - o2.getId();
        }
    };

}