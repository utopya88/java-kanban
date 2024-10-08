package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epic;

    public Subtask(String name, String description, Status status, int epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public Subtask(String name, String description, Status status, int epic, Duration duration, LocalDateTime startTime) {
        super(name, description, status,startTime,duration);
        this.epic = epic;
    }

    public Subtask(int id, String name, String description, Status status, int epic,Duration duration,
                   LocalDateTime startTime) {
        super(id, name, description, status,startTime,duration);
        this.epic = epic;
    }

    public int getEpic() {
        return epic;
    }

    public void setEpic(int epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return getId() + ",Подзадача," + getName() + "," + getStatus() + "," + getDescription() + ","
                + getEpic() + ";";
    }
}