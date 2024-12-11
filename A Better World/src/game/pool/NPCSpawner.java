package game.pool;

import game.entity.NPC;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;
import game.state.PlayState;

import java.util.ArrayList;
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
        for(NPC npc : new ArrayList<>(npcs)) {
            npc.deactivate();
            NPCPool.instance.returnNPC(npc, npc.getType());
            PlayState.removeNPC(npc);
            npcs.remove(npc);
        }

    }
}
