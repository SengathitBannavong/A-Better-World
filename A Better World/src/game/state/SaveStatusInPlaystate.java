package game.state;

import game.entity.Player;
import game.physic.Vector2D;

public class SaveStatusInPlaystate {
    private int health_player;
    private Vector2D playerPosition;
    private int mapIndex;

    // Singleton
    private static SaveStatusInPlaystate instance = null;

    private SaveStatusInPlaystate(){

    }

    public static synchronized SaveStatusInPlaystate getInstance(){
        if(instance == null){
            instance = new SaveStatusInPlaystate();
        }
        return instance;
    }

    public void setHealthPlayer(int health){
        this.health_player = health;
    }

    public int getHealthPlayer(){
        return this.health_player;
    }

    public void setPlayerPosition(Vector2D position){
        this.playerPosition = position;
    }

    public Vector2D getPlayerPosition(){
        return this.playerPosition;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public void setMapIndex(int mapIndex) {
        this.mapIndex = mapIndex;
    }

    public void reset(){
        this.health_player = 0;
        this.playerPosition = null;
        this.mapIndex = -1;
    }

    public void load(Player player,int mapIndex){
        // Load status from file
        this.health_player = player.getHp();
        this.playerPosition = player.getOrigin();
        this.mapIndex = mapIndex;
    }

}
