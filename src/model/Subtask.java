package model;

public class Subtask extends Task {

    private int epic;

    public Subtask(String name, String description, Status status, int epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public Subtask(int id, String name, String description, Status status, int epic) {
        super(id, name, description, status);
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
        return getId() + "," + Type.SUBTASK + "," + getName() + "," + getStatus() + "," + getDescription() + ","
                + getEpic() + ";";
    }
}