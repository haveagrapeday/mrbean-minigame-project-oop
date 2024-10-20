package mrbeangame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class GamePanel extends JPanel implements ActionListener {

    private Image background, character, obstacle, nextStory1, nextStory2, startScreen, tryAgain, teddy;
    private Timer timer;
    private int bgX = 0;
    private int characterY = 400;
    private int velocityY = 0;
    private boolean jumping = false;
    private int obstacleX = 800;
    private int teddyX = 1000;
    private boolean gameOver = false;
    private int score = 0;

    private enum GameState {
        STORY1, STORY2, INSTRUCTIONS, GAME, GAME_OVER
    }
    private GameState gameState = GameState.STORY1;

    private JButton nextButton, startButton, tryAgainButton;
    private AudioClip collectSound;

    public GamePanel() {
        loadImages();
        loadSounds();
        timer = new Timer(16, this);
        timer.start();

        setLayout(null);

        
        nextButton = new JButton("Next");
        nextButton.setBounds(300, 500, 100, 50);
        nextButton.addActionListener(e -> {
            if (gameState == GameState.STORY1) {
                gameState = GameState.STORY2;
                repaint();
            } else if (gameState == GameState.STORY2) {
                gameState = GameState.INSTRUCTIONS;
                remove(nextButton);
                add(startButton);
                repaint();
            }
        });

        
        startButton = new JButton("Start Game");
        startButton.setBounds(300, 500, 120, 50);
        startButton.addActionListener(e -> {
            if (gameState == GameState.INSTRUCTIONS) {
                gameState = GameState.GAME;
                remove(startButton);
                requestFocusInWindow();
                repaint();
            }
        });

        
        tryAgainButton = new JButton("Try Again");
        tryAgainButton.setBounds(300, 500, 120, 50);
        tryAgainButton.addActionListener(e -> {
            if (gameState == GameState.GAME_OVER) {
                restartGameWithoutStory();
                remove(tryAgainButton);
                requestFocusInWindow();
                repaint();
            }
        });

        add(nextButton);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (gameState == GameState.GAME && !jumping) {
                        jumping = true;
                        velocityY = -30; 
                    }
                }
            }
        });
    }

    private void loadImages() {
        background = new ImageIcon(getClass().getResource("/resources/background.png")).getImage();
        character = new ImageIcon(getClass().getResource("/resources/character.png")).getImage();
        obstacle = new ImageIcon(getClass().getResource("/resources/obstacle.png")).getImage();
        nextStory1 = new ImageIcon(getClass().getResource("/resources/NEXT_STORY.PNG")).getImage();
        nextStory2 = new ImageIcon(getClass().getResource("/resources/NEXT_STORY.PNG")).getImage();
        startScreen = new ImageIcon(getClass().getResource("/resources/START.PNG")).getImage();
        tryAgain = new ImageIcon(getClass().getResource("/resources/TRY_AGAIN.PNG")).getImage();
        teddy = new ImageIcon(getClass().getResource("/resources/teddy.png")).getImage();
    }

    private void loadSounds() {
        try {
            URL url = getClass().getResource("/resources/collect.wav");
            collectSound = Applet.newAudioClip(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (gameState) {
            case STORY1 ->
                g.drawImage(nextStory1, 0, 0, getWidth(), getHeight(), this);
            case STORY2 ->
                g.drawImage(nextStory2, 0, 0, getWidth(), getHeight(), this);
            case INSTRUCTIONS ->
                g.drawImage(startScreen, 0, 0, getWidth(), getHeight(), this);
            case GAME -> {
                g.drawImage(background, bgX, 0, getWidth(), getHeight(), this);
                g.drawImage(background, bgX + getWidth(), 0, getWidth(), getHeight(), this);

                int characterWidth = character.getWidth(null);
                int characterHeight = character.getHeight(null);
                g.drawImage(character, 100, characterY - characterHeight + 30, characterWidth * 2, characterHeight * 2, this);
                g.drawImage(obstacle, obstacleX, 430, this);
                g.drawImage(teddy, teddyX, 350, this);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("Score: " + score, 20, 40);
            }
            case GAME_OVER -> {
                g.drawImage(tryAgain, 0, 0, getWidth(), getHeight(), this);
                add(tryAgainButton);
            }
        }
    }

    @Override
public void actionPerformed(ActionEvent e) {
    if (gameState == GameState.GAME && !gameOver) {
        bgX -= 5;
        if (bgX <= -getWidth()) {
            bgX = 0;
        }

        if (jumping) {
            characterY += velocityY;
            velocityY += 1;
            if (characterY >= 400) {
                characterY = 400;
                jumping = false;
                velocityY = 0;
            }
        } else {
            if (characterY < 400) {
                characterY += velocityY;
            }
        }

        obstacleX -= 5;
        if (obstacleX < -64) {
            obstacleX = getWidth();
        }

        teddyX -= 5;
        if (teddyX < -64) {
            teddyX = getWidth() + (int) (Math.random() * 300) + 200;
        }

        int characterWidth = character.getWidth(null) * 2;
        int obstacleWidth = obstacle.getWidth(null);

        
        if (obstacleX < 20 + characterWidth - 1 && obstacleX + obstacleWidth > 50 + 10 && characterY >= 401 && characterY <= 401) { 
            gameOver = true;
            gameState = GameState.GAME_OVER;
            timer.stop();
        }

        int teddyWidth = teddy.getWidth(null);
        if (teddyX < 100 + characterWidth && teddyX + teddyWidth > 100 && characterY >= 380) {
            score += 10;
            if (collectSound != null) {
                collectSound.play();
            }
            teddyX = getWidth() + (int) (Math.random() * 300) + 200;
        }

        repaint();
    }
}




    private void restartGameWithoutStory() {
        gameOver = false;
        characterY = 400; 
        velocityY = 0; 
        obstacleX = 800;
        teddyX = 1000;
        bgX = 0;
        score = 0;
        gameState = GameState.GAME;
        timer.start();
    }
}
