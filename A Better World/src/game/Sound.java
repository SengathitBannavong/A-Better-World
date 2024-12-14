package game;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

// TODO make dash pick up item and hurt
public class Sound {
    private Clip clip;
    private URL[] soundURL = new URL[30];

    // volume control
    private FloatControl gainControl;

    public Sound() {
        // Initialize sound URLs
        soundURL[0] = getClass().getResource("/sound/OnceUponATime.wav");
        soundURL[1] = getClass().getResource("/sound/FallenDown.wav");
        soundURL[2] = getClass().getResource("/sound/Determination.wav");
        soundURL[3] = getClass().getResource("/sound/Home.wav");
        soundURL[4] = getClass().getResource("/sound/X_vs_Zero.wav");
        soundURL[5] = getClass().getResource("/sound/attack.wav");
        soundURL[6] = getClass().getResource("/sound/end.wav");
        soundURL[7] = getClass().getResource("/sound/loot.wav");
        soundURL[8] = getClass().getResource("/sound/pass.wav");
        soundURL[9] = getClass().getResource("/sound/HopeAndDream.wav");
        soundURL[10] = getClass().getResource("/sound/startgame.wav");
        soundURL[11] = getClass().getResource("/sound/classic_hurt.wav");

        // Debug: Print out initialized URLs
        for (int i = 0; i < soundURL.length; i++) {
            if (soundURL[i] != null) {
                System.out.println("soundURL[" + i + "] = " + soundURL[i]);
            }
        }
    }

    public void setFile(int i) {
        System.out.println("setFile called with index: " + i);
        // Stop any currently playing clip before opening a new one
        stop();
        try {
            if (soundURL[i] == null) {
                System.out.println("File âm thanh không tồn tại tại chỉ số: " + i);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
            System.out.println("File âm thanh tại chỉ số " + i + " đã được khởi tạo.");

            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume((float) MenuPanel.valueMusic);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    public void play() {
        if (clip != null) {
            System.out.println("Playing sound...");
            clip.start();
        } else {
            System.out.println("Clip chưa được khởi tạo.");
        }
    }

    public void loop() {
        if (clip != null) {
            System.out.println("Looping sound...");
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            System.out.println("Clip chưa được khởi tạo.");
        }
    }

    public void stop() {
        if (clip != null) {
            System.out.println("Stopping and closing sound...");
            clip.stop();
            clip.flush();
            clip.close();
            clip = null;
        } else {
            // It's possible that this is called without a clip set, just a debug message
            System.out.println("No clip to stop.");
        }
    }

    public void setVolume(float volume) {
        if (gainControl != null) {
            // Convert volume (0 to 100) to a linear volume (0.0 to 1.0)
            float linearVolume = volume / 100f;
            // Convert linear scale to decibels
            float dB;
            if (linearVolume == 0) {
                dB = gainControl.getMinimum();
            } else {
                dB = (float) (20.0 * Math.log10(linearVolume));
                if (dB < gainControl.getMinimum()) {
                    dB = gainControl.getMinimum();
                } else if (dB > gainControl.getMaximum()) {
                    dB = gainControl.getMaximum();
                }
            }
            gainControl.setValue(dB);
            System.out.println("Volume set to: " + volume + " (dB: " + dB + ")");
        } else {
            System.out.println("Gain control chưa được khởi tạo.");
        }
    }
}
