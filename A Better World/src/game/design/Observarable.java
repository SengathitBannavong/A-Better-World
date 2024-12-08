package game.design;

public interface Observarable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
