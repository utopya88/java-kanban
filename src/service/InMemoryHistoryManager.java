package service;

import model.CustomLinkedList;
import model.Node;
import model.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList historyList = new CustomLinkedList();

    @Override
    public void add(Task task) {
        int id = task.getId();
        if (historyList.getHistory().containsKey(id)) {
            Node node = historyList.getHistory().get(id);
            historyList.linkLast(task);
            historyList.removeNode(node);
        } else {
            historyList.linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void remove(int id) {
        if (!historyList.getHistory().containsKey(id)) {
            return;
        }
        Node node = historyList.getHistory().remove(id);
        historyList.removeNode(node);
    }
}
