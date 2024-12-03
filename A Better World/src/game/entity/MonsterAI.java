package game.entity;

import game.Debug;
import game.design.Observer;
import game.enum_.F_Direction;
import game.movement.BasicMovement;
import game.movement.MovementStrategy;
import game.physic.Vector2D;

public class MonsterAI implements Observer {

    private MovementStrategy movementStrategy;
    private Vector2D lastPlayerPosition;
    private Monster monster;

    public MonsterAI(Monster monster, Player player) {
        this.monster = monster;
        player.addObserver(this);
        movementStrategy = new BasicMovement();
    }

    @Override
    public void update(Player player) {
        lastPlayerPosition = player.getOrigin();
    }

    public void update(){
        if(lastPlayerPosition != null){
            if(playerInRange()) {
                chasePlayer();
            }else{
                patrol();
            }
        }
    }

    // where player left or right or up or down
    private void setDirection(Vector2D direction){
       if(direction.x > 0){
           monster.movement_dir[F_Direction.RIGHT.ordinal()] = true;
           monster.movement_dir[F_Direction.LEFT.ordinal()] = false;
       }else if(direction.x < 0){
           monster.movement_dir[F_Direction.RIGHT.ordinal()] = false;
           monster.movement_dir[F_Direction.LEFT.ordinal()] = true;
       }

       if(direction.y > 0){
           monster.movement_dir[F_Direction.DOWN.ordinal()] = true;
           monster.movement_dir[F_Direction.UP.ordinal()] = false;
       }else if(direction.y < 0){
           monster.movement_dir[F_Direction.DOWN.ordinal()] = false;
           monster.movement_dir[F_Direction.UP.ordinal()] = true;
       }
    }

    private boolean playerInRange() {
        if(Debug.debugging){
            System.out.println("Player distance: " + monster.getOrigin().distance(lastPlayerPosition));
        }
        return monster.getOrigin().distance(lastPlayerPosition) < 650;
    }

    private void chasePlayer(){
        Vector2D direction = lastPlayerPosition.subtract(monster.getOrigin()).normalize();
        // when monster is close to player, stop moving
        if(monster.getOrigin().distance(lastPlayerPosition) < 10){
            direction = new Vector2D(0, 0);
        }
        setDirection(direction);
        movementStrategy.move(monster);
    }

    private void patrol(){
        Vector2D patrolPoint = new Vector2D(10, 10);
        Vector2D patrolPoint2 = new Vector2D(20, 20);
        if(monster.getOrigin().distance(patrolPoint) < 10){
            setDirection(patrolPoint2.subtract(monster.getOrigin()).normalize());
        }else if(monster.getOrigin().distance(patrolPoint2) < 10){
            setDirection(patrolPoint.subtract(monster.getOrigin()).normalize());
        }
        movementStrategy.move(monster);
    }
}
