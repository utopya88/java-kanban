package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;

    private final Gson gson = Managers.getGson();

    public HttpTaskManager(String url) {
        super(Managers.getDefaultHistory());
        this.client = new KVTaskClient(url);
        load();
    }

    @Override
    public void save() {
        String tasksJson = gson.toJson(tasks);
        String epicJson = gson.toJson(epics);
        String subTaskJson = gson.toJson(subTasks);
        String historyJson = gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        client.put("tasks", tasksJson);
        client.put("epics", epicJson);
        client.put("subtasks", subTaskJson);
        client.put("history", historyJson);
    }

    @Override
    public void load() {
        String responseTasks = client.load("tasks");
        String responseEpic = client.load("epics");
        String responseSubTasks = client.load("subtasks");
        String responseHistory = client.load("history");
        int maxTasksKey = 0;
        int maxEpicsKey = 0;
        int maxSubTasksKey = 0;
        if (!responseTasks.isEmpty()) {
            Type taskType = new TypeToken<HashMap<Integer, Task>>() {
            }.getType();
            tasks = gson.fromJson(responseTasks, taskType);
            if (!tasks.isEmpty()) {
                maxTasksKey = Collections.max(tasks.keySet());
            }
        }
        if (!responseEpic.isEmpty()) {
            Type epicType = new TypeToken<HashMap<Integer, Epic>>() {
            }.getType();
            epics = gson.fromJson(responseEpic, epicType);
            if (!epics.isEmpty()) {
                maxEpicsKey = Collections.max(epics.keySet());
            }
        }
        if (!responseSubTasks.isEmpty()) {
            Type subTaskType = new TypeToken<HashMap<Integer, SubTask>>() {
            }.getType();
            subTasks = gson.fromJson(responseSubTasks, subTaskType);
            if (!subTasks.isEmpty()) {
                maxSubTasksKey = Collections.max(subTasks.keySet());
            }
        }
        seq = Math.max(Math.max(maxTasksKey, maxEpicsKey), maxSubTasksKey);

        Type historyType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        List<Integer> historyList = gson.fromJson(responseHistory, historyType);
        if (historyList != null) {
            for (Integer id : historyList) {
                if (tasks.containsKey(id)) {
                    historyManager.add(tasks.get(id));
                }
                if (epics.containsKey(id)) {
                    historyManager.add(epics.get(id));
                }
                if (subTasks.containsKey(id)) {
                    historyManager.add(subTasks.get(id));
                }
            }
        }
    }
}
