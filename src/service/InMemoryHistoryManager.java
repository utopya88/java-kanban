package service;

import java.util.ArrayList;
import java.util.List;
import model.Task;

public class InMemoryHistoryManager implements HistoryManager {
    protected List<Task> historyList = new ArrayList<>();
    @Override
    public void add(Task task) {
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return (ArrayList)((ArrayList)historyList).clone();
    }
}
