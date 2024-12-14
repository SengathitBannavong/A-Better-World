package game.entity;

import game.Debug;
import game.GamePanel;
import game.design.Observer;
import game.enum_.F_Direction;
import game.enum_.F_Statue_Animate;
import game.enum_.F_Type_Sprite_Entity;
import game.movement.BasicMovement;
import game.movement.MovementStrategy;
import game.physic.AABB;
import game.physic.Vector2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;

public class MonsterAI implements Observer<Player> {

    private MovementStrategy movementStrategy;
    private Vector2D lastPlayerPosition;
    private Vector2D monsterPosition;
    private Monster monster;

    // Distances for different behaviors
    private int attackRange = 75;    // Range at which the monster attacks
    private int visionRange = 300;   // Range at which the monster can see the player

    // Boss adjustments (Type3)
    private int bossAttackRange = 125;
    private int bossVisionRange = 500;

    private boolean bossSeePlayer = false;
    private boolean bossSeePlayerOneTime = false;

    // AI states
    private enum State {
        IDLE,
        CHASING,
        ATTACKING
    }

    private State currentState = State.IDLE;

    // Store player's last known velocity to predict movement (for boss)
    private Vector2D playerLastKnownPosition;
    private Vector2D playerLastKnownVelocity = new Vector2D(0,0);
    private AABB playerHitbox;

    public MonsterAI(Monster monster, Player player) {
        this.monster = monster;
        movementStrategy = new BasicMovement();
        player.addObserver(this);

        // Adjust AI parameters if the monster is a boss (Type3)
        if (monster.getType() == F_Type_Sprite_Entity.Type3) {
            attackRange = bossAttackRange;
            visionRange = bossVisionRange;
        }
    }

    @Override
    public void updateListener(Player player) {
        Vector2D newPlayerPos = player.getOrigin().add(new Vector2D(136,136));

        // Update player velocity (approximation)
        if (playerLastKnownPosition != null) {
            playerLastKnownVelocity = newPlayerPos.subtract(playerLastKnownPosition);
        }
        playerLastKnownPosition = newPlayerPos;

        lastPlayerPosition = newPlayerPos;
        playerHitbox = player.getHitbox();
    }

    public void update() {
        if (lastPlayerPosition == null) {
            // No player known: stay idle
            setFalse();
            return;
        }

        monsterPosition = monster.getOrigin().add(new Vector2D(monster.getSize(), monster.getSize()));
        if(monster.getType() == F_Type_Sprite_Entity.Type3){
            monsterPosition = monsterPosition.add(new Vector2D(0,32));
        }

        double distance = monsterPosition.distance(lastPlayerPosition);
        boolean canSeePlayer = (distance < visionRange) || hasLineOfSight();
        if(monster.getType() == F_Type_Sprite_Entity.Type3){
            if(canSeePlayer){
                bossSeePlayer = true;
            }
            if(bossSeePlayer && !bossSeePlayerOneTime){
                bossSeePlayerOneTime = true;
                GamePanel.playMusic(4);
            }
        }
        chasePlayer();
        switch (currentState) {
            case IDLE:
                if (canSeePlayer) {
                    currentState = State.CHASING;
                } else {
                    // Just idle (stop moving)
                    setFalse();
                }
                break;

            case CHASING:
                if (!canSeePlayer) {
                    // Lost sight of player, switch to idle
                    currentState = State.IDLE;
                } else if (distance < attackRange && !monster.isStillAttack() && hasLineOfSight()) {
                    currentState = State.ATTACKING;
                } else {
                    chasePlayer();
                }
                break;

            case ATTACKING:
                if (!canSeePlayer) {
                    // If player disappears during attack, become idle
                    currentState = State.IDLE;
                } else if ((distance > attackRange && !monster.isStillAttack()) || !hasLineOfSight()) {
                    // Player moved away from attack range, chase again
                    currentState = State.CHASING;
                } else {
                    // Attack logic
                    attackPlayer();
                }
                break;
        }

        movementStrategy.move(monster);
    }

    private void chasePlayer() {
        Vector2D targetPos = lastPlayerPosition;

        // If boss, try predictive movement: aim where the player might be next
        if (monster.getType() == F_Type_Sprite_Entity.Type3 && playerLastKnownVelocity.magnitude() > 0.1) {
            targetPos = predictPlayerPosition();
        }

        Vector2D direction = targetPos.subtract(monsterPosition);
        float dotx = direction.dot(new Vector2D(1, 0));
        float doty = direction.dot(new Vector2D(0, -1));

        setDirection(dotx, doty);
    }

    private void attackPlayer() {
        // Basic attack
        monster.SetstateAnimation(F_Statue_Animate.Attack);
        setFalse();
    }

    private Vector2D predictPlayerPosition() {
        // Predictive targeting for boss
        float predictionFactor = 0.5f;
        return lastPlayerPosition.add(playerLastKnownVelocity.multiply(predictionFactor));
    }

    // Simple line-of-sight check (no obstacles)
    private boolean hasLineOfSight() {
        return monster.getMonsterSightofLight().collides(playerHitbox);
    }

    private void setDirection(float dotX, float dotY) {
        setFalse();
        // Direction logic
        if (Math.abs(dotX) > Math.abs(dotY)) {
            if (dotX > 0) {
                monster.movement_dir[F_Direction.RIGHT.ordinal()] = true;
            } else {
                monster.movement_dir[F_Direction.LEFT.ordinal()] = true;
            }

            if (Math.abs(dotY) > 10) {
                if (dotY > 0) monster.movement_dir[F_Direction.UP.ordinal()] = true;
                else monster.movement_dir[F_Direction.DOWN.ordinal()] = true;
            }
        } else {
            if (dotY > 0) {
                monster.movement_dir[F_Direction.UP.ordinal()] = true;
            } else {
                monster.movement_dir[F_Direction.DOWN.ordinal()] = true;
            }

            if (Math.abs(dotX) > 10) {
                if (dotX > 0) monster.movement_dir[F_Direction.RIGHT.ordinal()] = true;
                else monster.movement_dir[F_Direction.LEFT.ordinal()] = true;
            }
        }
    }

    private void setFalse() {
        Arrays.fill(monster.movement_dir, false);
    }

    public void render(Graphics2D g) {
        if (monsterPosition == null || lastPlayerPosition == null) return;
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(2));
        g.draw(new Line2D.Double(
                monsterPosition.getWorldVar().x,
                monsterPosition.getWorldVar().y,
                lastPlayerPosition.getWorldVar().x,
                lastPlayerPosition.getWorldVar().y
        ));

    }
}
