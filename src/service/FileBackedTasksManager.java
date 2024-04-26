package service;

import model.*;

import exceptions.ManagerSaveException;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    protected Path path;

    static final String HEADER = "id,type,name,status,description,epic\n";


    public void save() {
        try (FileWriter fileRecord = new FileWriter(path.toString())) {
            fileRecord.write(HEADER);
            for (Integer key : tasks.keySet()) {
                fileRecord.write(tasks.get(key).toString() + "\n");
            }
            for (Integer key : epics.keySet()) {
                fileRecord.write(epics.get(key).toString() + "\n");
            }
            for (Integer key : subTasks.keySet()) {
                fileRecord.write(subTasks.get(key).toString() + "\n");
            }
            fileRecord.write("\n");
            if (!historyManager.getHistory().isEmpty()) {
                fileRecord.write(historyToString(historyManager));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка записи в файл", e);
        }
    }
    private static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Integer.valueOf(split[i]));
        }
        return list;
    }  private static String historyToString(HistoryManager historyManager) {
        List<Task> list = historyManager.getHistory();
        int counter = 0;
        StringBuilder builder = new StringBuilder();
        for (Task task : list) {
            if (counter < list.size() - 1) {
                builder.append(task.getId());
                builder.append(",");
            } else {
                builder.append(task.getId());
            }
            counter++;
        }
        return builder.toString();
    }

    private Task fromString(String value) {
        String[] columns = value.split(",");
        int id = Integer.parseInt(columns[0]);
        Status status = Status.valueOf(columns[3]);
        Type type = Type.valueOf(columns[1]);


        Task task;
        switch (type) {
            case TASK:
                task = new Task(id, columns[2], columns[4], Status.valueOf(columns[3]));
                break;

            case EPIC:
                task = new Epic(id, columns[2], columns[4]);
                task.setStatus(status);
                break;

            case SUBTASK:
                task = new Subtask(id, columns[2], columns[4], Status.valueOf(columns[3]), Integer.parseInt(columns[5]));
                break;

            default:
                throw new ManagerSaveException("Неизвестный тип объекта " + type);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubTask(Subtask subTask) {
        Subtask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }
}
