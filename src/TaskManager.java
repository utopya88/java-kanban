
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
public class TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subTasks;
    protected int seq = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();

    }
    private int generateId() {
        return ++seq;
    }


    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubTasks() {
        return subTasks;
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }
    public Subtask createSubTask(Subtask subTask) {
        int epicId = subTask.getEpic();
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epicId);
        epic.addSubtaskId(subTask.getId());
        epic.setStatus(calculateStatus(epic));
        return subTask;
    }
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        return task;
    }
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        return epic;
    }

    public Subtask getSubTaskById(int id) {
        Subtask subTask = subTasks.get(id);
        return subTask;
    }
    public void updateTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        tasks.put(task.getId(), task);
    }
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }
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

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic saved = epics.get(id);
        for (Integer subTaskIdForDelete : saved.getSubTasksId()) {
            subTasks.remove(subTaskIdForDelete);
        }
        epics.remove(id);
    }

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

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            epic.setStatus(calculateStatus(epic));
        }
    }
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

}