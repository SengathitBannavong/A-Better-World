package game.object;

import game.physic.AABB;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Map_teleport {
    List<BoxTP> boxTPs;

    public Map_teleport(List<BoxTP> boxTPs) {
        this.boxTPs = boxTPs;
    }

    public List<BoxTP> getBoxTPs() {
        return boxTPs;
    }

    public void setBoxTPs(List<BoxTP> boxTPs) {
        this.boxTPs = boxTPs;
    }

    public void render(Graphics2D g){
        for(BoxTP boxTP : boxTPs){
            boxTP.render(g);
        }
    }

    public BoxTP checkCollision(AABB player){
        for(BoxTP boxTP : boxTPs){
            if(boxTP.getAABB().collides(player)){
                return boxTP;
            }
        }
        return null;
    }

}
