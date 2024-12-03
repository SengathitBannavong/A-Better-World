package game.pool;

import game.entity.Monster;
import game.physic.Vector2D;
import game.state.PlayState;

import java.util.List;


public class MonsterSpawner {


    public MonsterSpawner() {
    }

    public void spawnMonster(Vector2D position) {
        Monster monster = MonsterPool.instance.getMonster();
            if(monster != null) {
                monster.activate();
                monster.setPos(position);
                monster.setHitbox(position);
                PlayState.addMonster(monster);
            }

    }

    public void despawnMonster(List<Monster> monsters) {
        for(Monster monster : monsters) {
            monster.deactivate();
            PlayState.removeMonster(monster);
            MonsterPool.instance.returnMonster(monster);
        }

    }

}
