package game.movement;

import game.entity.Entity;
import game.enum_.F_Direction;

public class BasicMovement implements MovementStrategy {

    private float dx;
    private float dy;
    private float acc;
    private float de_acc;
    private int maxSpeed;
    @Override
    public void move(Entity entity) {
        handleMovement(entity, entity.getMovement_dir(), entity.isMoving());
    }

    private void handleMovement(Entity entity,boolean[] direction, boolean isMoving) {
        dx = entity.getDx();
        dy = entity.getDy();
        acc = entity.getAcc();
        de_acc = entity.getDe_acc();
        maxSpeed = entity.getMaxSpeed();
        // Basic Movement case
        if(isMoving && direction[F_Direction.UP.ordinal()]) {
            acc_moveup();
        }else{
            deacc_moveup();
        }

        if(isMoving && direction[F_Direction.DOWN.ordinal()]) {
            acc_movedown();
        }else{
            deacc_movedown();
        }

        if(isMoving && direction[F_Direction.LEFT.ordinal()]) {
            acc_moveleft();
        }else{
            deacc_moveleft();
        }

        if(isMoving && direction[F_Direction.RIGHT.ordinal()]) {
            acc_moveright();
        }else{
            deacc_moveright();
        }

        entity.setDx(dx);
        entity.setDy(dy);
    }


    private void acc_moveup(){
        dy -= acc;
        if(dy < -maxSpeed){
            dy = -maxSpeed;
        }
    }
    private void acc_movedown(){
        dy += acc;
        if (dy > maxSpeed) {
            dy = maxSpeed;
        }
    }
    private void acc_moveleft(){
        dx -= acc;
        if(dx < -maxSpeed){
            dx = -maxSpeed;
        }
    }
    private void acc_moveright(){
        dx += acc;
        if (dx > maxSpeed) {
            dx = maxSpeed;
        }
    }

    private void deacc_moveup(){
        if(dy < 0){
            dy += de_acc;
            if(dy > 0){
                dy = 0;
            }
        }
    }
    private void deacc_movedown(){
        if(dy > 0){
            dy -= de_acc;
            if(dy < 0){
                dy = 0;
            }
        }
    }
    private void deacc_moveleft(){
        if(dx < 0){
            dx += de_acc;
            if(dx > 0){
                dx = 0;
            }
        }
    }
    private void deacc_moveright(){
        if(dx > 0){

            dx -= de_acc;
            if(dx < 0){
                dx = 0;
            }
        }
    }

}
