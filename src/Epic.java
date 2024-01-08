import java.util.ArrayList;
public class Epic extends Task{
    ArrayList<Integer> subTasksId = new ArrayList<>(); //айди сабтасков

    public Epic(String name, String description, ArrayList<Integer> subTasks) {
        super(name, description);
        this.subTasksId = subTasks;
    }
}
