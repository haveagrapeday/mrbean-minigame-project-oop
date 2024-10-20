
package mrbeangame;
import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Mr. Bean Jumping Game");
        setSize(800, 600);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
    }
}
