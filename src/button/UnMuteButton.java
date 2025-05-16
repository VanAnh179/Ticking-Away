package button;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import main.Sound;

public class UnMuteButton extends JButton {
    private ImageIcon muteIcon, unmuteIcon;
    private boolean isMuted;
    private Sound musicPlayer;

    public UnMuteButton(Sound musicPlayer) {
        super("");

        this.musicPlayer = musicPlayer; // Nhận đối tượng Sound từ MenuScreen
        muteIcon = new ImageIcon("assets/button/Mute.jpg");
        unmuteIcon = new ImageIcon("assets/button/Unmute.png");
        setIcon(unmuteIcon);
        isMuted = false;

        addActionListener(e -> {
            isMuted = !isMuted; // Đảo trạng thái mute/unmute
            setIcon(isMuted ? muteIcon : unmuteIcon);
            repaint();

            if (isMuted) {
                musicPlayer.stop(); // Dừng nhạc
            } else {
                musicPlayer.play();
                musicPlayer.loop(); // Bật lại nhạc
            }
        });
    }
}
