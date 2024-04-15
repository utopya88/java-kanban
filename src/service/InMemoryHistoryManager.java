package service;

import java.util.ArrayList;
import java.util.List;
import model.Node;
import model.CustomLinkedList;
import model.Task;

public class InMemoryHistoryManager implements HistoryManager {
    protected CustomLinkedList historyList = new CustomLinkedList();
    @Override
    public void add(Task task) {
        int id = task.getId();
        if (historyList.getHistory().containsKey(id)) {
            Node node = historyList.getHistory().get(id);
            historyList.linkList(task);
            historyList.removeNode(node);
        } else {
            historyList.linkList(task);
        }

    }

    @Override
    public void remove(int id) {
        if (!historyList.getHistory().containsKey(id)) {
            return;
        }
        Node node = historyList.getHistory().remove(id);
        historyList.removeNode(node);
    }
    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }
}
