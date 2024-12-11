package game.Input;

import game.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class KeyHandler implements KeyListener {

    public static List<Key> keys = new ArrayList<Key>();

    public static class Key{
        public int presses = 0, absorbs = 3;
        public boolean down, clicked;

        public Key(){
            keys.add(this);
        }

        public void toggle(boolean pressed){
            if(pressed != down){
                down = pressed;
            }
            if(down){
                presses++;
            }
        }

        public void tick(){
            if(absorbs < presses){
                absorbs++;
                clicked = down;
            }else{
                clicked = down;
            }
        }
    }

    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key menu = new Key();
    public Key enter = new Key();
    public Key escape = new Key();
    public Key dash = new Key();
    public Key pause = new Key();
    public Key write = new Key();
    public Key cell = new Key();
    public Key deletecell = new Key();
    public Key collition = new Key();
    public Key nextMap = new Key();
    public Key spawn = new Key();
    public Key despawn = new Key();
    public Key talking = new Key();
    public Key skip = new Key();
    public Key test = new Key();

    public KeyHandler(GamePanel game){
        game.addKeyListener(this);
    }

    public void releaseAll(){
        for (Key key : keys) {
            key.down = false;
        }
    }

    public void tick(){
        for (Key key : keys) {
            key.tick();
        }
    }

    public void toggle(KeyEvent e, boolean pressed){
        // Toggle keys based on key events
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> up.toggle(pressed);
            case KeyEvent.VK_S -> down.toggle(pressed);
            case KeyEvent.VK_A -> left.toggle(pressed);
            case KeyEvent.VK_D -> right.toggle(pressed);
            case KeyEvent.VK_E -> menu.toggle(pressed);
            case KeyEvent.VK_ENTER -> enter.toggle(pressed);
            case KeyEvent.VK_ESCAPE -> escape.toggle(pressed);
            case KeyEvent.VK_SHIFT -> dash.toggle(pressed);
            case KeyEvent.VK_5 -> pause.toggle(pressed);
            case KeyEvent.VK_1 -> write.toggle(pressed);
            case KeyEvent.VK_0 -> cell.toggle(pressed);
            case KeyEvent.VK_9 -> deletecell.toggle(pressed);
            case KeyEvent.VK_MINUS -> collition.toggle(pressed);
            case KeyEvent.VK_Q -> nextMap.toggle(pressed);
            case KeyEvent.VK_I -> spawn.toggle(pressed);
            case KeyEvent.VK_O -> despawn.toggle(pressed);
            case KeyEvent.VK_C -> talking.toggle(pressed);
            case KeyEvent.VK_SPACE -> skip.toggle(pressed);
            case KeyEvent.VK_T -> test.toggle(pressed);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do anything
    }

    @Override
    public void keyPressed(KeyEvent e) {
        toggle(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        toggle(e, false);
    }
}

