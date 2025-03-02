package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;

    Gson gson = Managers.getGson();

    Task task1;
    Epic epic2;
    SubTask subTask3;

    @BeforeEach
    void beforeEach() throws IOException {

        taskManager = Managers.getDefaultInMemoryManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.startServer();
        LocalDateTime startTime1 = LocalDateTime.of(2023, 5, 8, 1, 0);
        task1 = taskManager.createTask(new Task("Task #1", "DT", Status.NEW, startTime1, Duration.ofMinutes(9)));
        epic2 = taskManager.createEpic(new Epic("Epic #2", "DE"));
        subTask3 = taskManager.createSubTask(new SubTask("SubTask #3", "DS", Status.NEW, 2, startTime1.plusMinutes(20), Duration.ofMinutes(6)));
        taskManager.getTaskById(1);
    }

    @AfterEach
    void afterEach() {
        taskServer.stopServer();
    }

    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(200, response.statusCode(), "Should return code 200");
        assertNotNull(tasks, "Should be not null");
        Task actual = tasks.get(0);
        assertEquals(task1, actual, "Task from history should be equal to Task1");
    }

    @Test
    void shouldRemoveSubTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Should return code 200");
        assertNull(taskManager.getSubTaskById(3), "Should return null");
    }

    @Test
    void shouldRemoveEpicTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Should return code 200");
        assertNull(taskManager.getEpicById(2), "Should return null");
    }

    @Test
    void shouldRemoveTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Should return code 200");
        assertNull(taskManager.getTaskById(1), "Should return null");
    }

    @Test
    void shouldRemoveAllSubTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Should return code 200");
        assertEquals(0, taskManager.getAllSubTasks().size(), "Should return 0");
    }

    @Test
    void shouldRemoveAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Should return code 200");
        assertEquals(0, taskManager.getAllEpics().size(), "Should return 0");
    }

    @Test
    void shouldRemoveAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Should return code 200");
        assertEquals(0, taskManager.getAllTasks().size(), "Should return 0");
    }

    @Test
    void shouldRemoveAll() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Should return code 200");
        assertEquals(0, taskManager.getAllTasks().size(), "Should return 0");
        assertEquals(0, taskManager.getAllEpics().size(), "Should return 0");
        assertEquals(0, taskManager.getAllSubTasks().size(), "Should return 0");

        List<Task> list = taskManager.getHistory();
        assertEquals(0, list.size(), "Should return 0");
    }

    @Test
    void shouldChangeSubTaskById() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask = new SubTask(3, "SubTask #4444", "DE", Status.IN_PROGRESS,2, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");
        assertEquals(subTask, taskManager.getSubTaskById(3), "Should be equal to subTask");
    }

    @Test
    void shouldChangeEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(2, "NEW Epic", "NEW DE");
        String json = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");
        assertEquals(epic, taskManager.getEpicById(2), "Should be equal to epic");
    }

    @Test
    void shouldChangeTaskById() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Task task = new Task(1, "NewTask #4444", "DE", Status.IN_PROGRESS, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");
        assertEquals(task, taskManager.getTaskById(1), "Should be equal to task");
    }

    @Test
    void shouldCreateNewSubTask() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        SubTask subTask = new SubTask("NEW #4444", "DE", Status.NEW,2, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Should return code 201");
        assertEquals(4, taskManager.getSubTaskById(4).getId(), "SubTask id in memory should be 4");
        assertEquals("NEW #4444", taskManager.getSubTaskById(4).getName(), "SubTask name in memory should be New #444");

        String jsonObject = response.body();
        SubTask subTask1 = gson.fromJson(jsonObject, SubTask.class);

        assertEquals(taskManager.getSubTaskById(4), subTask1, "SubTask in response should be equals to memory");
    }

    @Test
    void shouldCreateNewEpic() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Epic epic = new Epic("NEW #4444", "DE");
        String json = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Should return code 201");
        assertEquals(4, taskManager.getEpicById(4).getId(), "Epic id in memory should be 4");
        assertEquals("NEW #4444", taskManager.getEpicById(4).getName(), "Epic name in memory should be New #444");

        String jsonObject = response.body();
        Epic epic1 = gson.fromJson(jsonObject, Epic.class);

        assertEquals(taskManager.getEpicById(4), epic1, "Epic in response should be equals to memory");
    }

    @Test
    void shouldCreateNewTask() throws IOException, InterruptedException {
        LocalDateTime startTime2 = LocalDateTime.of(2023, 5, 8, 1, 10);
        Task task = new Task("NEW #4444", "DE", Status.NEW, startTime2, Duration.ofMinutes(5));
        String json = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(),"Should return code 201");
        assertEquals(4, taskManager.getTaskById(4).getId(), "Task id in memory should be 4");
        assertEquals("NEW #4444", taskManager.getTaskById(4).getName(), "Task name in memory should be New #444");

        String jsonObject = response.body();
        Task task1 = gson.fromJson(jsonObject, Task.class);

        assertEquals(taskManager.getTaskById(4), task1, "Task in response should be equals to memory");
    }

    @Test
    void shouldReceiveSubTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");

        String jsonObject = response.body();
        SubTask subTask1 = gson.fromJson(jsonObject, SubTask.class);

        assertEquals(taskManager.getSubTaskById(3), subTask1, "SubTask in response should be equals to memory");
    }

    @Test
    void shouldReceiveEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");

        String jsonObject = response.body();
        Epic epic = gson.fromJson(jsonObject, Epic.class);

        assertEquals(taskManager.getEpicById(2), epic, "SubTask in response should be equals to memory");
    }

    @Test
    void shouldReceiveTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");

        String jsonObject = response.body();
        Task task = gson.fromJson(jsonObject, Task.class);

        assertEquals(taskManager.getTaskById(1), task, "SubTask in response should be equals to memory");
    }

    @Test
    void shouldReceiveAllSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");
        List<SubTask> list = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>(){}.getType());

        assertEquals(list.get(0), taskManager.getAllSubTasks().get(0), "SubTask in response should be equals to subtask from method");
        assertEquals(list.size(), taskManager.getAllSubTasks().size(), "Sizes of the lists should be equal");
    }

    @Test
    void shouldReceiveAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");
        List<Epic> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>(){}.getType());

        assertEquals(list.get(0), taskManager.getAllEpics().get(0), "SubTask in response should be equals to subtask from method");
        assertEquals(list.size(), taskManager.getAllEpics().size(), "Sizes of the lists should be equal");
    }

    @Test
    void shouldReceiveAllTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");
        List<Task> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(list.get(0), taskManager.getAllTasks().get(0), "SubTask in response should be equals to subtask from method");
        assertEquals(list.size(), taskManager.getAllTasks().size(), "Sizes of the lists should be equal");
    }

    @Test
    void shouldReceiveAll() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),"Should return code 200");
        List<Task> list = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());
        assertEquals(3, list.size(), "Sizes of the list should be 3");
    }
}
