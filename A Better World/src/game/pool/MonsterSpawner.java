package game.pool;

import game.entity.Monster;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;
import game.state.PlayState;

import java.util.ArrayList;
import java.util.List;


public class MonsterSpawner {


    public MonsterSpawner() {
    }

    public static void spawnMonster(Vector2D position, F_Type_Sprite_Entity type) {
        Monster monster = MonsterPool.instance.getMonster(type);
            if(monster != null) {
                monster.activate();
                monster.setPos(position);
                monster.setHitbox(position);
                monster.InitHp();
                PlayState.addMonster(monster);
                System.out.println("Monster id:"+ monster.getId() +" spawned");
            }

    }

    public static void despawnMonster(List<Monster> monsters) {
        for (Monster monster : new ArrayList<>(monsters)) { // Create a copy of the list
            monster.deactivate();
            MonsterPool.instance.returnMonster(monster, monster.getType());
            PlayState.removeMonster(monster); // Modify the original list
            monsters.remove(monster); // Remove monster safely from the original list
            System.out.println("Monster id:" + monster.getId() + " despawned");
        }
    }

}
