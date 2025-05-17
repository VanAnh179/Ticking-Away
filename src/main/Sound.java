package main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[30];
    
    public Sound() {
        soundURL[0] = getClass().getResource("/sound/menusound.wav");
        soundURL[1] = getClass().getResource("/sound/gamesound.wav");
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
        if (clip != null && !clip.isRunning()) {
            clip.start(); // Tiếp tục từ vị trí dừng
        }
    }
    
    public void loop() {
        if (clip != null) clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop(); // Chỉ dừng, KHÔNG đóng clip
        }
    }
}