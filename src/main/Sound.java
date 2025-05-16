package main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	Clip clip;
	URL soundURL[] = new URL[30];
	
	public Sound() {
		//soundURL[0] = getClass().getResource(null);
		soundURL[0] = getClass().getResource("/sound/sound.wav");
	}
	
	public void setFile(int i) {
		try {
			if(soundURL[i] == null) {
				System.out.println("Sound file not found");
				return;
			}
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
		} catch (Exception e) {
			System.out.println("Error with setting sound file");
			e.printStackTrace();	
		}
	}
	
	public void play() {
		if(clip != null) {
			clip.setFramePosition(0);
			clip.start();
		}
	}
	
	public void loop() {
		
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
		if(clip != null && clip.isRunning()) {
			clip.stop();
		}
	}
}
