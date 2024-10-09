package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer implements HttpHandler {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = Managers.getGson();
    private final TaskManager taskManager;
    private final HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefault();
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", this::handle);
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.taskManager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", this::handle);
    }

    public void startServer() {
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        int choice = endpoint(path, method, body, query);

        String response = "Обработка запроса клиента";

        switcher(choice, exchange, body, taskManager);

        exchange.sendResponseHeaders(200, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void switcher(int choice, HttpExchange exchange, String body, TaskManager taskManager) {
        switch (choice) {
            case 1:
                outputAll(exchange, taskManager);
                break;
            case 2:
                outputAllTasks(exchange, taskManager);
                break;
            case 3:
                outputAllEpics(exchange, taskManager);
                break;
            case 4:
                outputAllSubTasks(exchange, taskManager);
                break;
            case 5:
                writeResponse(exchange, "Неверный запрос", 400);
            case 6:
                receiveTaskById(exchange, taskManager);
                break;
            case 7:
                receiveEpicById(exchange, taskManager);
                break;
            case 8:
                receiveSubTaskById(exchange, taskManager);
                break;
            case 9:
                createNewTask(body, exchange, taskManager);
                break;
            case 10:
                createNewEpic(body, exchange, taskManager);
                break;
            case 11:
                createNewSubTask(body, exchange, taskManager);
                break;
            case 12:
                changeTask(body, exchange, taskManager);
                break;
            case 13:
                changeEpic(body, exchange, taskManager);
                break;
            case 14:
                changeSubTask(body, exchange, taskManager);
                break;
            case 15:
                removeAll(exchange, taskManager);
                break;
            case 16:
                removeAllTasks(exchange, taskManager);
                break;
            case 17:
                removeAllEpics(exchange, taskManager);
                break;
            case 18:
                removeAllSubTasks(exchange, taskManager);
                break;
            case 19:
                removeTaskById(exchange, taskManager);
                break;
            case 20:
                removeEpicById(exchange, taskManager);
                break;
            case 21:
                removeSubTaskById(exchange, taskManager);
                break;
            case 22:
                receiveHistory(exchange, taskManager);
                break;
        }

    }

    private int endpoint(String path, String method, String body, String query) {
        String[] pathParts = path.split("/");
        if (method.equals("GET")) {
            if (pathParts.length == 2) return 1;
            else if (pathParts.length == 3 && query == null) {
                if (pathParts[2].equals("task")) return 2; // получаем список тасков
                else if (pathParts[2].equals("epic")) return 3; // получаем список эпиков
                else if (pathParts[2].equals("subtask")) return 4; // получаем список сабтасков
                else if (pathParts[2].equals("history")) return 22; // получаем историю
                else return 5; // неправильный запрос
            } else if (pathParts.length == 3) {
                String[] queryParts = query.split("=");
                if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                    return 5; // неправильный запрос
                try {
                    Integer.parseInt(queryParts[1]); // проверяем, что второе значение параметра запроса - число.
                    if (pathParts[2].equals("task")) return 6; // получаем таску по айди
                    else if (pathParts[2].equals("epic")) return 7; // получаем эпик по айди
                    else if (pathParts[2].equals("subtask")) return 8; // получаем сабтаск по айди
                    else return 5;// неправильный запрос
                } catch (NumberFormatException e) {
                    return 5; // неправильный запрос
                }
            } else return 5;
        } else if (method.equals("POST")) {
            if (body.isBlank()) return 5; // неправильный запрос
            if (pathParts.length == 3 && query == null) {
                if (pathParts[2].equals("task")) return 9; // создаем таску
                else if (pathParts[2].equals("epic")) return 10; // создаем эпик
                else if (pathParts[2].equals("subtask")) return 11; // создаем сабтаск
                else return 5; //неправильный запрос
            } else if (pathParts.length == 3) {
                String[] queryParts = query.split("=");
                if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                    return 5; //неправильный запрос
                try {
                    Integer.parseInt(queryParts[1]); // проверяем, что второе значение параметра запроса - число.
                    if (pathParts[2].equals("task")) return 12; // изменяем таску по айди
                    else if (pathParts[2].equals("epic")) return 13; // изменяем эпик по айди
                    else if (pathParts[2].equals("subtask")) return 14; // изменяем сабтаск по айди
                    else return 5;//неправильный запрос
                } catch (NumberFormatException e) {
                    return 5; //неправильный запрос
                }
            } else return 5;
        } else if (method.equals("DELETE")) {
            if (pathParts.length == 2) return 15; // удалить все эпики, таски, сабтаск
            else if (pathParts.length == 3 && query == null) {
                if (pathParts[2].equals("task")) return 16; // удаление всех тасков
                else if (pathParts[2].equals("epic")) return 17; // удаление всех эпиков
                else if (pathParts[2].equals("subtask")) return 18; // удаление всех сабтасков
                else return 5; // неправильный запрос
            } else if (pathParts.length == 3) {
                String[] queryParts = query.split("=");
                if (queryParts.length <= 1 || !queryParts[0].equals("id") || queryParts.length > 2)
                    return 5; //неправильный запрос
                try {
                    Integer.parseInt(queryParts[1]); // проверяем, что второе значение параметра запроса - число.
                    if (pathParts[2].equals("task")) return 19; // удаляем таску по айди
                    else if (pathParts[2].equals("epic")) return 20; // удаление всех эпик по айди
                    else if (pathParts[2].equals("subtask")) return 21; // удаление всех сабтаск по айди
                    else return 5;//неправильный запрос
                } catch (NumberFormatException e) {
                    return 5; //неправильный запрос
                }
            } else return 5; //неправильный запрос
        }
        return 5; //неправильный запрос
    }

    private void receiveHistory(HttpExchange exchange, TaskManager tasksManager) {
        String stringJson = gson.toJson(tasksManager.getHistory());
        writeResponse(exchange, stringJson, 200);
    }

    private void removeSubTaskById(HttpExchange exchange, TaskManager tasksManager) {
        String query = exchange.getRequestURI().getQuery();
        String[] queryParts = query.split("=");
        int id = Integer.parseInt(queryParts[1]);
        tasksManager.deleteSubTaskById(id);
        writeResponse(exchange, "Сабтаск с айди " + id + "удалена", 200);
    }

    private void removeEpicById(HttpExchange exchange, TaskManager tasksManager) {
        String query = exchange.getRequestURI().getQuery();
        String[] queryParts = query.split("=");
        int id = Integer.parseInt(queryParts[1]);
        tasksManager.deleteEpicById(id);
        writeResponse(exchange, "Эпик с айди " + id + "удалена", 200);
    }

    private void removeTaskById(HttpExchange exchange, TaskManager tasksManager) {
        String query = exchange.getRequestURI().getQuery();
        String[] queryParts = query.split("=");
        int id = Integer.parseInt(queryParts[1]);
        tasksManager.deleteTaskById(id);
        writeResponse(exchange, "Задача с айди " + id + "удалена", 200);
    }

    private void removeAllSubTasks(HttpExchange exchange, TaskManager tasksManager) {
        tasksManager.deleteAllSubTasks();
        writeResponse(exchange, "Все подзадачи удалены", 200);
    }

    private void removeAllEpics(HttpExchange exchange, TaskManager tasksManager) {
        tasksManager.deleteAllEpics();
        writeResponse(exchange, "Все эпики удалены", 200);
    }

    private void removeAllTasks(HttpExchange exchange, TaskManager tasksManager) {
        tasksManager.deleteAllTasks();
        writeResponse(exchange, "Все задачи удалены", 200);
    }

    private void removeAll(HttpExchange exchange, TaskManager tasksManager) {
        tasksManager.deleteAllTasks();
        tasksManager.deleteAllSubTasks();
        tasksManager.deleteAllEpics();
        writeResponse(exchange, "Все задачи, эпики и подзадачи удалены", 200);
    }

    private void changeSubTask(String body, HttpExchange exchange, TaskManager tasksManager) {
        try {
            SubTask subtask = gson.fromJson(body, SubTask.class);
            tasksManager.updateSubTask(subtask);
            writeResponse(exchange, "Подзадача изменена", 200);
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорреткный JSON", 400);
        }
    }

    private void changeEpic(String body, HttpExchange exchange, TaskManager tasksManager) {
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            tasksManager.updateEpic(epic);
            writeResponse(exchange, "Эпик изменен", 200);
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорреткный JSON", 400);
        }
    }

    private void changeTask(String body, HttpExchange exchange, TaskManager tasksManager) {
        try {
            Task task = gson.fromJson(body, Task.class);
            tasksManager.updateTask(task);
            writeResponse(exchange, "Задача изменена", 200);
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорреткный JSON", 400);
        }
    }

    private void createNewSubTask(String body, HttpExchange exchange, TaskManager tasksManager) {
        try {
            SubTask subtask = gson.fromJson(body, SubTask.class);
            SubTask newSubTask = tasksManager.createSubTask(subtask);
            String epicJson = gson.toJson(newSubTask);
            writeResponse(exchange, epicJson, 201);
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорреткный JSON", 400);
        }
    }

    private void createNewEpic(String body, HttpExchange exchange, TaskManager tasksManager) {
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            Epic newEpic = tasksManager.createEpic(epic);
            String epicJson = gson.toJson(newEpic);
            writeResponse(exchange, epicJson, 201);
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорреткный JSON", 400);
        }
    }

    private void createNewTask(String body, HttpExchange exchange, TaskManager tasksManager) {
        try {
            Task task = gson.fromJson(body, Task.class);
            Task newTask = tasksManager.createTask(task);
            String taskJson = gson.toJson(newTask);
            writeResponse(exchange, taskJson, 201);
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорреткный JSON", 400);
        }
    }

    private void receiveSubTaskById(HttpExchange exchange, TaskManager tasksManager) {
        String query = exchange.getRequestURI().getQuery();
        String[] queryParts = query.split("=");
        String stringJson = gson.toJson(tasksManager.getSubTaskById(Integer.parseInt(queryParts[1])));
        writeResponse(exchange, stringJson, 200);
    }

    private void receiveEpicById(HttpExchange exchange, TaskManager tasksManager) {
        String query = exchange.getRequestURI().getQuery();
        String[] queryParts = query.split("=");
        String stringJson = gson.toJson(tasksManager.getEpicById(Integer.parseInt(queryParts[1])));
        writeResponse(exchange, stringJson, 200);
    }

    private void receiveTaskById(HttpExchange exchange, TaskManager tasksManager) {
        String query = exchange.getRequestURI().getQuery();
        String[] queryParts = query.split("=");
        String stringJson = gson.toJson(tasksManager.getTaskById(Integer.parseInt(queryParts[1])));
        writeResponse(exchange, stringJson, 200);
    }

    private void outputAllSubTasks(HttpExchange exchange, TaskManager tasksManager) {
        String stringJson = gson.toJson(tasksManager.getAllSubTasks());
        writeResponse(exchange, stringJson, 200);
    }

    private void outputAllEpics(HttpExchange exchange, TaskManager tasksManager) {
        String stringJson = gson.toJson(tasksManager.getAllEpics());
        writeResponse(exchange, stringJson, 200);
    }

    private void outputAllTasks(HttpExchange exchange, TaskManager tasksManager) {
        String stringJson = gson.toJson(tasksManager.getAllTasks());
        writeResponse(exchange, stringJson, 200);
    }

    private void outputAll(HttpExchange exchange, TaskManager tasksManager) {
        List<Task> list = new ArrayList<>(tasksManager.getAllTasks());
        list.addAll(tasksManager.getAllEpics());
        list.addAll(tasksManager.getAllSubTasks());
        String listJson = gson.toJson(list);
        writeResponse(exchange, listJson, 200);
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) {
        if (responseString.isBlank()) {
            try {
                exchange.sendResponseHeaders(responseCode, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            try {
                exchange.sendResponseHeaders(responseCode, bytes.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        exchange.close();
    }

}
