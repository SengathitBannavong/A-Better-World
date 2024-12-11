package game.state;

import game.entity.Monster;
import game.entity.NPC;
import game.entity.Player;

import java.util.List;

public class SaveStatusInPlaystate {
    private int health_player;
    private List<Monster> activeMonsters;
    private List<NPC> activeNPCs;
}
