package game.graphic;

import java.awt.image.BufferedImage;

public class Animation {
    private BufferedImage[] frames;
    private int currentFrame;
    private int numFrames;

    private int count_delay;
    private int delay;

    private int timesPlayed;

    public Animation() {
        timesPlayed = 0;
    }

    public Animation(BufferedImage[] frames) {
        timesPlayed = 0;
        setFrames(frames);
    }

    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
        currentFrame = 0;
        count_delay = 0;
        timesPlayed = 0;
        delay = 2;
        numFrames = frames.length;
    }

    public void setDelay(int i) { delay = i; }

    public void setFrame(int i) { currentFrame = i; }

    public void setNumFrames(int i) { numFrames = i; }

    public void setCount_delay(int i) { count_delay = i; }

    public int getFrame() { return currentFrame; }

    public int getCount_delay() { return count_delay; }

    public int getDelay() { return delay; }

    public BufferedImage getImage() { return frames[currentFrame]; }

    public int getNumFrames() { return numFrames; }

    public void update() {
        if(delay == -1) return;
        count_delay++;

        if(count_delay == delay) {
            currentFrame++;
            count_delay = 0;
        }

        if(currentFrame == numFrames) {
            currentFrame = 0;
            timesPlayed++;
        }
    }

    public int getTimesPlayed() { return timesPlayed; }

    public boolean hasPlayedOnce() {
        return timesPlayed > 0;
    }

    public boolean hasPlayed(int i) {
        return timesPlayed == i;
    }
}
