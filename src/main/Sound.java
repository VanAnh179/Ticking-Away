package main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    public Clip clip;
    private long pausePosition = 0;
    URL soundURL[] = new URL[30];
    
    public Sound() {
        soundURL[0] = getClass().getResource("/sound/menusound.wav");
        soundURL[1] = getClass().getResource("/sound/gamesound.wav");
        soundURL[2] = getClass().getResource("/sound/explode.wav");
        soundURL[3] = getClass().getResource("/sound/hurtPlayer.wav");
        soundURL[4] = getClass().getResource("/sound/hurtE1.wav");
        soundURL[5] = getClass().getResource("/sound/deathE.wav");
        soundURL[6] = getClass().getResource("/sound/hurtE2.wav");
        soundURL[7] = getClass().getResource("/sound/hurtE3.wav");
        soundURL[8] = getClass().getResource("/sound/fire-sound.wav");
        soundURL[9] = getClass().getResource("/sound/key.wav");
        soundURL[10] = getClass().getResource("/sound/walking.wav");
        soundURL[11] = getClass().getResource("/sound/text.wav");
        soundURL[12] = getClass().getResource("/sound/tutorial_bm.wav");
        soundURL[13] = getClass().getResource("/sound/win_jojo.wav");
    }
    
    public void setFile(int i) {
        try {
            if (soundURL[i] == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();    
        }
    }
    
    public void play() {
        if (clip != null) {
            clip.stop();        // Dừng clip hiện tại
            clip.setFramePosition(0); // Reset về đầu
            clip.start(); // Tiếp tục từ vị trí dừng
        }
    }
    
    public void loop() {
        if (clip != null) clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.flush(); // Thêm dòng này để xóa buffer
            clip.setFramePosition(0); // Reset về đầu
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.setMicrosecondPosition(pausePosition);
            clip.start();
        }
    }
}