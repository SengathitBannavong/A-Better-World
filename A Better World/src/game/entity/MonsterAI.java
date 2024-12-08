package game.entity;

import game.Debug;
import game.GamePanel;
import game.design.Observer;
import game.enum_.F_Direction;
import game.movement.BasicMovement;
import game.movement.MovementStrategy;
import game.physic.Vector2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;

public class MonsterAI implements Observer<Player> {

    private MovementStrategy movementStrategy;
    private Vector2D lastPlayerPosition;
    private Vector2D monsterPosition;
    private Monster monster;

    public MonsterAI(Monster monster, Player player) {
        this.monster = monster;
        player.addObserver(this);
        movementStrategy = new BasicMovement();
    }

    @Override
    public void update(Player player) {
        lastPlayerPosition = player.getOrigin();
        lastPlayerPosition = lastPlayerPosition.add(new Vector2D(136, 136));
    }

    public void update(){
        if(lastPlayerPosition != null){
            monsterPosition = monster.getOrigin();
            monsterPosition = monsterPosition.add(new Vector2D(64, 64));
            if(playerInRange()) {
                chasePlayer();
            }else{
                //patrol();
            }
        }
    }

    // where player left or right or up or down
    private void setDirection(float dotX,float dotY){
        setFalse();
        if(dotX >= -64 && dotX <= 64){
            if(dotY > 0){
                monster.movement_dir[F_Direction.UP.ordinal()] = true;
                return;
            }else if(dotY < 0){
                monster.movement_dir[F_Direction.DOWN.ordinal()] = true;
                return;
            }
        }

        if(dotY >= -64 && dotY <= 64){
            if(dotX > 0){
                monster.movement_dir[F_Direction.RIGHT.ordinal()] = true;
                return;
            }else if(dotX < 0){
                monster.movement_dir[F_Direction.LEFT.ordinal()] = true;
                return;
            }
        }

        if(dotX > 0){
            monster.movement_dir[F_Direction.RIGHT.ordinal()] = true;
        }else if(dotX < 0){
            monster.movement_dir[F_Direction.LEFT.ordinal()] = true;
        }

        if(dotY > 0){
            monster.movement_dir[F_Direction.UP.ordinal()] = true;
        }else if(dotY < 0){
            monster.movement_dir[F_Direction.DOWN.ordinal()] = true;
        }
    }



    private boolean playerInRange() {
        if(Debug.debugging){
           // System.out.println("Player distance: " + monster.getOrigin().distance(lastPlayerPosition));
        }
        return monster.getOrigin().distance(lastPlayerPosition) < 1;
    }

    private void chasePlayer(){
        Vector2D direction = lastPlayerPosition.subtract(monsterPosition);
        float dotx = direction.dot(new Vector2D(1, 0));
        float doty = direction.dot(new Vector2D(0, -1));
        // when monster is close to player, stop moving
        if(monsterPosition.distance(lastPlayerPosition) < 10){
            setFalse();
        }else {
            setDirection(dotx, doty);
        }
        movementStrategy.move(monster);
    }

    private void setFalse(){
        Arrays.fill(monster.movement_dir, false);
    }

//    private void patrol(){
//        Vector2D patrolPoint = new Vector2D(10, 10);
//        Vector2D patrolPoint2 = new Vector2D(20, 20);
//
//        if(monsterPosition.distance(patrolPoint) < 10){
//            setDirection();
//        }else if(monsterPosition.distance(patrolPoint2) < 10){
//            setDirection();
//        }
//        movementStrategy.move(monster);
//    }

    public void render(Graphics2D g){
        // Draw line of sight
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(2));
        g.draw(new Line2D.Double(monsterPosition.getWorldVar().x, monsterPosition.getWorldVar().y, lastPlayerPosition.getWorldVar().x, lastPlayerPosition.getWorldVar().y));
    }
}
