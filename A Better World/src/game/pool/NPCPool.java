// src/game/pool/NPCPool.java
package game.pool;

import game.entity.NPC;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class NPCPool {
    public static NPCPool instance;
    private final Map<F_Type_Sprite_Entity, Queue<NPC>> npcs;
    private final int maxNPCs;
    private final NPCFactory npcFactory;

    private NPCPool(int maxNPCs, NPCFactory npcFactory, int size) {
        this.maxNPCs = maxNPCs;
        this.npcFactory = npcFactory;
        this.npcs = new HashMap<>();

        for (int i = 1; i <= size; i++) {
            Queue<NPC> queue = new LinkedList<>();
            F_Type_Sprite_Entity type = getTypeByI(i);
            for (int j = 0; j < maxNPCs; j++) {

                queue.add(npcFactory.createEntity(new Vector2D(), type));
            }
            npcs.put(type, queue);
        }
        System.out.println("============ NPCPool created ==========");
    }

    // Singleton pattern
    public static synchronized NPCPool getInit(int maxNPCs, NPCFactory npcFactory,int ScropOfTypes) {
        if (instance == null) {
            instance = new NPCPool(maxNPCs, npcFactory,ScropOfTypes);
        }
        return instance;
    }

    public synchronized NPC getNPC(F_Type_Sprite_Entity type) {
        Queue<NPC> queue = npcs.get(type);
        if (queue != null && !queue.isEmpty()) {
            return queue.poll();
        }
        return null;
    }

    public synchronized void returnNPC(NPC npc, F_Type_Sprite_Entity type) {
        Queue<NPC> queue = npcs.get(type);
        if (queue != null && queue.size() < maxNPCs) {
            npc.deactivate();
            queue.offer(npc);
        } else {
            System.out.println("Max NPCs reached for type: " + type);
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