import java.util.ArrayList;
import java.util.Objects;
import java.util.List;


public class Epic extends Task{
    private final List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }
    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public void addSubtaskId(int subTaskId) {
        subTasks.add(subTaskId);
    }
    public List<Integer> getSubTasksId() {
        return subTasks;
    }

    public void clearSubtask() {
        subTasks.clear();
    }

    public void clearSubtaskForId(int subTaskId) {
        subTasks.remove(subTaskId);
    }

    @Override
    public String toString() {
        return getId() + ",Эпик," + getName() + "," + getStatus() + "," + getDescription() + ";";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return (getId() == epic.getId()) &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getDescription(), epic.getDescription());
    }
}
