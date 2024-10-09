package model;

public enum Status {
    NEW("Новый"),
    IN_PROGRESS("В процессе") ,
    DONE("Завершен");

    String nameStatus;

    Status(String nameStatus) {
        this.nameStatus = nameStatus;
    }

    String getName() {
        return nameStatus;
    }

    public void setNameStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }
}
