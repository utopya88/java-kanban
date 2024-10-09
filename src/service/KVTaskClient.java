package service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String url;
    private final String apiToken;
    private static final String HEADER_NAME = "Accept";
    private static final String HEADER_VALUE = "application/json";
    HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(String url) {
        this.url = url;
        this.apiToken = register();
    }

    private String register() {
        String registerUrl = url + "/register";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(registerUrl)).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка во время исполнения запроса, возможно данные на сервере отсутствуют");
        }
        return null;
    }

    public void put(String key, String json) {
        String registerUrl = url + "/save/" + key + "?API_TOKEN=" + apiToken;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(registerUrl))
                .header(HEADER_NAME, HEADER_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка во время исполнения запроса, возможно данные на сервере отсутствуют");
        }

    }

    public String load(String key) {
        String registerUrl = url + "/load/" + key + "?API_TOKEN=" + apiToken;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(registerUrl))
                .header(HEADER_NAME, HEADER_VALUE)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка во время исполнения запроса, возможно данные на сервере отсутствуют");
        }
        return "";
    }
}
