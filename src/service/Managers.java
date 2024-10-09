package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.DurationAdapter;
import server.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefaultInMemoryManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }
    
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}
