package game.pool;

import game.entity.Monster;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;
import game.state.PlayState;

import java.util.List;


public class MonsterSpawner {


    public MonsterSpawner() {
    }

    public void spawnMonster(Vector2D position) {
        Monster monster = MonsterPool.instance.getMonster(F_Type_Sprite_Entity.Type1);
            if(monster != null) {
                monster.activate();
                monster.setPos(position);
                monster.setHitbox(position);
                PlayState.addMonster(monster);
                System.out.println("Monster id:"+ monster.getId() +" spawned");
            }

    }

    public void despawnMonster(List<Monster> monsters) {
        for(Monster monster : monsters) {
            monster.deactivate();
            PlayState.removeMonster(monster);
            MonsterPool.instance.returnMonster(monster,F_Type_Sprite_Entity.Type1);
            System.out.println("Monster id:"+ monster.getId() +" despawned");
        }

    }

}
