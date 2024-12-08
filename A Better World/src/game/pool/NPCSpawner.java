package game.pool;

import game.entity.NPC;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;
import game.state.PlayState;

import java.util.List;

public class NPCSpawner {

    public NPCSpawner() {
    }

    public void spawnNPC(Vector2D position, F_Type_Sprite_Entity type) {
        NPC npc = NPCPool.instance.getNPC(type);
            if(npc != null) {
                npc.activate();
                npc.setPos(position);
                npc.setHitbox(position);
                PlayState.addNPC(npc);
            }

    }

    public void despawnNPC(List<NPC> npcs) {
        for(NPC npc : npcs) {
            npc.deactivate();
            PlayState.removeNPC(npc);
            NPCPool.instance.returnNPC(npc, npc.getType());
        }

    }
}
