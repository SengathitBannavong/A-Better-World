// src/game/pool/NPCPool.java
package game.pool;

import game.entity.Item;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ItemPool {
    public static ItemPool instance;
    private final Map<F_Type_Sprite_Entity, Queue<Item>> items;
    private final int maxItems;
    private final ItemFactory itemFactory;

    private ItemPool(int maxItems, ItemFactory itemFactory, int size) {
        this.maxItems = maxItems;
        this.itemFactory = itemFactory;
        this.items = new HashMap<>();

        for (int i = 1; i <= size; i++) {
            Queue<Item> queue = new LinkedList<>();
            F_Type_Sprite_Entity type = getTypeByI(i);
            //F_Item_Name name = getNameByI(i);
            for (int j = 0; j < maxItems; j++) {

                queue.add(itemFactory.createEntity(new Vector2D(), type));
            }
            items.put(type, queue);
        }
        System.out.println("============ ItemPool created ==========");
    }

    // Singleton pattern
    public static synchronized ItemPool getInit(int maxItems, ItemFactory itemFactory,int ScropOfTypes) {
        if (instance == null) {
            instance = new ItemPool(maxItems, itemFactory,ScropOfTypes);
        }
        return instance;
    }

    public synchronized Item getItem(F_Type_Sprite_Entity type) {
        Queue<Item> queue = items.get(type);
        if (queue != null && !queue.isEmpty()) {
            return queue.poll();
        }
        return null;
    }

    public synchronized void returnItem(Item item, F_Type_Sprite_Entity type) {
        Queue<Item> queue = items.get(type);
        if (queue != null && queue.size() < maxItems) {
            item.deactivate();
            queue.offer(item);
        } else {
            System.out.println("Max Items reached for type: " + type);
        }
    }

    private F_Type_Sprite_Entity getTypeByI (int i){
        return switch (i){
            case 1 -> F_Type_Sprite_Entity.Type1;
            case 2 -> F_Type_Sprite_Entity.Type2;
            case 3 -> F_Type_Sprite_Entity.Type3;
            default -> F_Type_Sprite_Entity.Type1;
        };
    }

//    public static F_Item_Name getNameByType(F_Type_Sprite_Entity type){
//        return switch (type){
//            case F_Type_Sprite_Entity.Type1 -> F_Item_Name.SHIELD;
//            case F_Type_Sprite_Entity.Type2 -> F_Item_Name.POWER;
//            case F_Type_Sprite_Entity.Type3 -> F_Item_Name.BLOOD;
//            default -> F_Item_Name.SHIELD;
//        };
//    }
//
//    private F_Item_Name getNameByI(int i){
//        return switch (i){
//            case 1 -> F_Item_Name.SHIELD;
//            case 2 -> F_Item_Name.POWER;
//            case 3 -> F_Item_Name.BLOOD;
//            default -> F_Item_Name.SHIELD;
//        };
//    }
//
//    public static F_Type_Sprite_Entity getTypeByName(F_Item_Name name){
//        return switch (name) {
//            case F_Item_Name.SHIELD -> F_Type_Sprite_Entity.Type1;
//            case F_Item_Name.POWER -> F_Type_Sprite_Entity.Type2;
//            case F_Item_Name.BLOOD -> F_Type_Sprite_Entity.Type3;
//            default -> F_Type_Sprite_Entity.Type1;
//        };
//    }


}