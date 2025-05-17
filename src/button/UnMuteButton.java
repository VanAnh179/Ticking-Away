package button;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import main.Sound;

public class UnMuteButton extends JButton {
    private ImageIcon muteIcon, unmuteIcon;
    private boolean isMuted = false;
    private Sound musicPlayer;

    public UnMuteButton(Sound musicPlayer) {
        super("");
        this.musicPlayer = musicPlayer;
        muteIcon = new ImageIcon("assets/button/Mute.jpg");
        unmuteIcon = new ImageIcon("assets/button/Unmute.png");
        setIcon(unmuteIcon);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);

        addActionListener(e -> toggleMute());
    }

    private void toggleMute() {
        isMuted = !isMuted;
        setIcon(isMuted ? muteIcon : unmuteIcon);

        if (isMuted) {
            musicPlayer.stop();
        } else {
            musicPlayer.play(); // Tiếp tục phát từ vị trí đã dừng
            musicPlayer.loop();
        }
    }
}