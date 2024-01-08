
import java.util.Objects;

public class Task {
    public String name;
    public String description;
    public Status status = Status.NEW;
    public int id;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        //id++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name=" + name + " " +
        ", description=" + description + " " +
        ", status=" + status +
                ", id=" + id +
                '}';
    }
}
