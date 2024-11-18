package game.state;

public class CachesState {
    private static GameState currentState = null;

    public CachesState() {
        currentState = null;
    }

    public static void setState(GameState state) {
        currentState = state;
    }

    public static GameState getState() {
        return currentState;
    }

    public static boolean isStateNull() {
        return currentState == null;
    }
}
