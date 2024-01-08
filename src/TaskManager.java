import java.util.HashMap;
public class TaskManager {
    public int id = 0;
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();

    public TaskManager () {
        id++;
    }
    public void AddTask(String name, String description) {
        Task task = new Task(name, description);
        taskMap.put(++task.id, task);
    }
    public void AddSubtask(String name, String description) {
        Subtask subtask = new Subtask(name, description);
        subtaskMap.put(++subtask.id, subtask);
    }
    public void AddEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        epicMap.put(++epic.id, epic);
    }
    public Task getTask(int id) {
        return taskMap.get(id);
    }
    public Subtask getSubtask(int id) {
        return subtaskMap.get(id);
    }
    public Epic getEpic (int id) {
        return epicMap.get(id);
    }
    public void removeTask(int id) {
        taskMap.remove(id);
    }
    public void removeSubtask(int id) {
        subtaskMap.remove(id);
    }
    public void removeEpic (int id) {
        epicMap.remove(id);
    }
    public void removeAllSubtask () {
        subtaskMap.clear();
    }
    public void removeAllEpic () {
        epicMap.clear();
    }

    public void removeAllTask() {
        taskMap.clear();
    }
    public HashMap<Integer, Task> PrintTaskMap() {
        return taskMap;
    }
    public HashMap<Integer, Epic> PrintEpicMap() {
        return epicMap;
    }
    public HashMap<Integer, Subtask> PrintSubtaskMap() {
        return subtaskMap;
    }

    // на каждый запрос 3 метода
    // изменить то , изменить 2 изменить 3
    // создание сабтаски посмотреть не нужно ли перевести эпик в дан
    // обновление по айди - пут в наш хешмап
    // удаление по айди если сабтаски все удалены = перевести в статус доне(вызвать метода обновление статуса эпика)
    // если удаляется эпик - то удаляются подзадачи
    // добавление новой задачи в эпик - меняется статус эпика
    // апдейт эпик, таск, сабтаск task.put(task.getId(), task))
    // id эпиков, тасков, сабтаск - сквозной


}