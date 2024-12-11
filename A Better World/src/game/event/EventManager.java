package game.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    public interface EventListener {
        void onEvent(String eventName, Object... args);
    }

    private static final List<EventListener> listeners = new ArrayList<>();

    public static void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    public static void triggerEvent(String eventName, Object... args) {
        for (EventListener listener : listeners) {
            listener.onEvent(eventName, args);
        }
    }
}
