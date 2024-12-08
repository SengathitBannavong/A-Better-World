package game.event;

import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String tag;
    private List<Vector2D> monsterSpawnPoints, npcSpawnPoints;
    private List<F_Type_Sprite_Entity> monsterTypes, npcTypes;
    private int capacityMonster, capacityNPC;
    private boolean IsEnd = false;

    public Event(String tag) {
        this.tag = tag;
        this.monsterSpawnPoints = new ArrayList<>();
        this.npcSpawnPoints = new ArrayList<>();
        this.monsterTypes = new ArrayList<>();
        this.npcTypes = new ArrayList<>();
        LoadData();
    }

    public boolean haveNpc(){
        return capacityNPC > 0;
    }

    public boolean haveMonster(){
        return capacityMonster > 0;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Vector2D> getMonsterSpawnPoints() {
        return monsterSpawnPoints;
    }

    public void setMonsterSpawnPoints(List<Vector2D> monsterSpawnPoints) {
        this.monsterSpawnPoints = monsterSpawnPoints;
    }

    public List<Vector2D> getNpcSpawnPoints() {
        return npcSpawnPoints;
    }

    public void setNpcSpawnPoints(List<Vector2D> npcSpawnPoints) {
        this.npcSpawnPoints = npcSpawnPoints;
    }

    public List<F_Type_Sprite_Entity> getMonsterTypes() {
        return monsterTypes;
    }

    public void setMonsterTypes(List<F_Type_Sprite_Entity> monsterTypes) {
        this.monsterTypes = monsterTypes;
    }

    public List<F_Type_Sprite_Entity> getNpcTypes() {
        return npcTypes;
    }

    public void setNpcTypes(List<F_Type_Sprite_Entity> npcTypes) {
        this.npcTypes = npcTypes;
    }

    public int getCapacityMonster() {
        return capacityMonster;
    }

    public void setCapacityMonster(int capacityMonster) {
        this.capacityMonster = capacityMonster;
    }

    public int getCapacityNPC() {
        return capacityNPC;
    }

    public void setCapacityNPC(int capacityNPC) {
        this.capacityNPC = capacityNPC;
    }

    private void LoadData(){
        // Load data from file
        System.out.print("Loading data from file " + tag);
        try (BufferedReader br = new BufferedReader(new FileReader("res/maps/"+ tag +"_Event.txt"))) {
            String line;
            line = br.readLine(); // read size of MonsterSpawnPoints
            String[] data = line.split(" ");
            int size = Integer.parseInt(data[0]);
            System.out.print(" sizeMonster: " + size);
            this.capacityMonster = size;
            if(size != 0) {
                GetData(br, size, monsterSpawnPoints, monsterTypes);// read MonsterSpawnPoints
            }
            line = br.readLine(); // read size of NpcSpawnPoints
            data = line.split(" ");
            size = Integer.parseInt(data[0]);
            System.out.println(" sizeNpc: " + size);
            this.capacityNPC = size;
            if(size != 0) {
                GetData(br, size, npcSpawnPoints, npcTypes); // read NpcSpawnPoints
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetData(BufferedReader br, int size, List<Vector2D> npcSpawnPoints, List<F_Type_Sprite_Entity> npcTypes) throws IOException {
        String line;
        for(int i = 1; i <= size; i++){
            // get next line
            line = br.readLine();
            String[] parts = line.split(" ");// x y type example: 100 100 Type1
            float x = Float.parseFloat(parts[0]);
            float y = Float.parseFloat(parts[1]);
            F_Type_Sprite_Entity type = F_Type_Sprite_Entity.valueOf(parts[2]);
            Vector2D temp = new Vector2D(x, y);
            npcSpawnPoints.add(temp);
            npcTypes.add(type);
        }
    }

    public boolean isEnd() {
        return IsEnd;
    }

    public void setEnd(boolean IsEnd) {
        this.IsEnd = IsEnd;
    }
}
