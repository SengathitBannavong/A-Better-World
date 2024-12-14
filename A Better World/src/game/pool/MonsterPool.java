// src/game/pool/MonsterPool.java
package game.pool;

import game.entity.Monster;
import game.entity.Player;
import game.enum_.F_Type_Sprite_Entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MonsterPool {
    public static MonsterPool instance;
    private final Map<F_Type_Sprite_Entity, Queue<Monster>> monsters;
    private final int maxMonsters;
    private final MonsterFactory monsterFactory;
    private final Player player;

    private MonsterPool(int maxMonsters, MonsterFactory monsterFactory, Player player,int size) {
        this.maxMonsters = maxMonsters;
        this.monsterFactory = monsterFactory;
        this.player = player;
        this.monsters = new HashMap<>();

        for (int i = 1; i <= size; i++) {
            Queue<Monster> queue = new LinkedList<>();
            F_Type_Sprite_Entity type = getTypeByI(i);
            for (int j = 0; j < maxMonsters; j++) {
                queue.add(monsterFactory.createEntity(player, type));
                if(type == F_Type_Sprite_Entity.Type3){
                   break;
                }
            }
            monsters.put(type, queue);
        }
        System.out.println("============ MonsterPool created ==========");
    }

    // Singleton pattern
    public static synchronized MonsterPool getInit(int maxMonsters, MonsterFactory monsterFactory, Player player,int ScropOfTypes) {
        if (instance == null) {
            instance = new MonsterPool(maxMonsters, monsterFactory, player,ScropOfTypes);
        }
        return instance;
    }

    public synchronized Monster getMonster(F_Type_Sprite_Entity type) {
        Queue<Monster> queue = monsters.get(type);
        if (queue != null && !queue.isEmpty()) {
            return queue.poll();
        }
        return null;
    }

    public synchronized void returnMonster(Monster monster, F_Type_Sprite_Entity type) {
        Queue<Monster> queue = monsters.get(type);
        if (queue != null && queue.size() < maxMonsters) {
            monster.deactivate();
            queue.offer(monster);
        } else {
            System.out.println("Max Monsters reached for type: " + type);
        }
    }

    private F_Type_Sprite_Entity getTypeByI(int i){
        return switch (i) {
            case 1 -> F_Type_Sprite_Entity.Type1;
            case 2 -> F_Type_Sprite_Entity.Type2;
            case 3 -> F_Type_Sprite_Entity.Type3;
            case 4 -> F_Type_Sprite_Entity.Type4;
            default -> F_Type_Sprite_Entity.Type1;
        };
    }
}