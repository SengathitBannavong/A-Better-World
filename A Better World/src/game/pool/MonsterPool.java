package game.pool;

import game.entity.Monster;
import game.entity.Player;

import java.util.LinkedList;
import java.util.Queue;

public class MonsterPool {
    public static MonsterPool instance;
    private final Queue<Monster> monsters;
    private final int maxMonsters;
    private final MonsterFactory monsterFactory;

    private MonsterPool(int maxMonsters, MonsterFactory monsterFactory, Player player) {
        this.maxMonsters = maxMonsters;
        this.monsterFactory = monsterFactory;
        this.monsters = new LinkedList<>();

        for (int i = 0; i < maxMonsters; i++) {
            monsters.add(monsterFactory.crateEntity(player));
        }
        System.out.println("============ MonsterPool created ==========");
    }

    // Singleton pattern
    public static synchronized MonsterPool getInit(int maxMonsters, MonsterFactory monsterFactory,Player player) {
        if(instance == null) {
            instance = new MonsterPool(maxMonsters, monsterFactory, player);
        }
        return instance;
    }

    public synchronized Monster getMonster() {
       if(!monsters.isEmpty()) {
           return monsters.poll();
       }
       return null;
    }

    public synchronized void returnMonster(Monster monster) {
        if(monsters.size() < maxMonsters) {
            monster.deactivate();
            monsters.offer(monster);
        }else{
            System.out.println("Max Monsters reached");
        }
    }
}
