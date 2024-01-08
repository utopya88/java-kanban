public class Subtask extends Task {
    int epicId; //подзадача эпика
    public Subtask(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name=" + name + " " +
        ", description=" + description + " " +
        ", status=" + status +
                ", id=" + id +
                '}';
    }
}