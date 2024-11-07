package mrbeangame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Mr. Bean Find Teddy");

        Image icon = new ImageIcon(getClass().getResource("/resources/teddy.png")).getImage();
        setIconImage(icon);

        setSize(800, 600);
        setResizable(false);
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
    }
}

public class GamePanel extends JPanel implements Runnable {

    private Image background, background2, background3, character, obstacle, nextStory1, nextStory2, startScreen, tryAgain, teddy;
    private Thread gameThread;
    private int bgX = 0;
    private int characterY = 400;
    private int velocityY = 0;
    private boolean jumping = false;
    private int obstacleX = 800;
    private int teddyX = 1000;
    private boolean gameOver = false;

    private int pick = 0;
    private int distanceScore = 0;
    private int lives = 3;
    private int heartCount = 3;
    private ImageIcon buttonbg, buttonbg2, buttonbg3;
    private Image myheart;
    private boolean collisionDetected;

    private void restartGameWithoutStory() {
        gameState = GameState.GAME;
        gameOver = false;
        bgX = 0;
        characterY = groundLevel;
        velocityY = 0;
        jumping = false;
        obstacleX = 800;
        teddyX = 1000;
        distanceScore = 0;
        pick = 0;
        heartCount = 3;
        HitCount = 0;
        hasCollided = false;
        currentBackground = 0;

        System.out.println("Game restarted! Heart count reset to: " + heartCount);
    }

    private boolean playerCollidesWithObstacle() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private enum GameState {
        STORY1, STORY2, INSTRUCTIONS, GAME, GAME_OVER
    }

    private GameState gameState = GameState.STORY1;

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private int HitCount = 0;
    private final int maxCollisions = 3;
    private Image hitEffect;
    private JButton nextButton, startButton, tryAgainButton;
    private JButton backgroundButton1, backgroundButton2, backgroundButton3;
    private int currentBackground = 0;

    private int groundLevel = 400;
    private int jumpHeight = 200;
    private boolean hasCollided = false;

    public GamePanel() {
        loadImages();
        setLayout(null);
        setFocusable(true);

        setupButtons();

        gameThread = new Thread(this);
        gameThread.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (gameState == GameState.GAME && !jumping) {
                        jumping = true;
                        velocityY = -20;
                    }
                }
            }
        });
    }

    private void loadImages() {
        nextStory1 = new ImageIcon(getClass().getResource("/resources/NEXT_STORY.PNG")).getImage();
        nextStory2 = new ImageIcon(getClass().getResource("/resources/NEXT_STORY.PNG")).getImage();
        startScreen = new ImageIcon(getClass().getResource("/resources/START.PNG")).getImage();
        tryAgain = new ImageIcon(getClass().getResource("/resources/TRY_AGAIN.PNG")).getImage();
        background = new ImageIcon(getClass().getResource("/resources/background.png")).getImage();
        background2 = new ImageIcon(getClass().getResource("/resources/background2.png")).getImage();
        background3 = new ImageIcon(getClass().getResource("/resources/background3.png")).getImage();
        buttonbg = new ImageIcon(getClass().getResource("/resources/buttonbg.png"));
        buttonbg2 = new ImageIcon(getClass().getResource("/resources/buttonbg2.png"));
        buttonbg3 = new ImageIcon(getClass().getResource("/resources/buttonbg3.png"));
        character = new ImageIcon(getClass().getResource("/resources/character.png")).getImage();
        obstacle = new ImageIcon(getClass().getResource("/resources/obstacle.png")).getImage();
        teddy = new ImageIcon(getClass().getResource("/resources/teddy.png")).getImage();
        hitEffect = new ImageIcon(getClass().getResource("/resources/HIT.PNG")).getImage();
        myheart = new ImageIcon(getClass().getResource("/resources/fullbean.png")).getImage();
    }

    private void setupButtons() {
        //-------->NEXT
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
        //-------->START
        startButton = new JButton("Start Game");
        startButton.setBounds(300, 500, 120, 50);
        startButton.addActionListener(e -> {
            if (gameState == GameState.INSTRUCTIONS) {
                gameState = GameState.GAME;
                remove(startButton);
                add(backgroundButton1);
                add(backgroundButton2);
                add(backgroundButton3);
                requestFocusInWindow();
                repaint();
            }
        });

        //-------->TRY AGAIN
        tryAgainButton = new JButton("Try Again");
        tryAgainButton.setBounds(300, 500, 120, 50);
        tryAgainButton.addActionListener(e -> {
            if (gameState == GameState.GAME_OVER) {
                restartGameWithoutStory();
                remove(tryAgainButton);
                add(backgroundButton1);
                add(backgroundButton2);
                add(backgroundButton3);
                requestFocusInWindow();
                repaint();
            }
        });

        //-------->BACKGROUND BUTTONS
        backgroundButton1 = new JButton(resizeIcon(buttonbg, 80, 80));
        backgroundButton1.setBounds(500, 10, 80, 80);
        backgroundButton1.setBorderPainted(false);
        backgroundButton1.setContentAreaFilled(false);
        backgroundButton1.setFocusPainted(false);
        backgroundButton1.setFocusable(false);
        backgroundButton1.addActionListener(e -> {
            currentBackground = 0;
            requestFocusInWindow();
            repaint();
        });
        backgroundButton2 = new JButton(resizeIcon(buttonbg2, 80, 80));
        backgroundButton2.setBounds(600, 10, 80, 80);
        backgroundButton2.setBorderPainted(false);
        backgroundButton2.setContentAreaFilled(false);
        backgroundButton2.setFocusPainted(false);
        backgroundButton2.setFocusable(false);
        backgroundButton2.addActionListener(e -> {
            currentBackground = 1;
            requestFocusInWindow();
            repaint();
        });
        backgroundButton3 = new JButton(resizeIcon(buttonbg3, 80, 80));
        backgroundButton3.setBounds(700, 10, 80, 80);
        backgroundButton3.setBorderPainted(false);
        backgroundButton3.setContentAreaFilled(false);
        backgroundButton3.setFocusPainted(false);
        backgroundButton3.setFocusable(false);
        backgroundButton3.addActionListener(e -> {
            currentBackground = 2;
            requestFocusInWindow();
            repaint();
        });

        add(nextButton);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (gameState == GameState.GAME && !jumping) {
                        jumping = true;
                        velocityY = -20;
                    }
                }
            }
        });
    }
    private boolean isColliding = false;
    private int collisionCount = 0;

    public void checkCollision() {
        if (isColliding) {

            return;
        }

        if (playerCollidesWithObstacle()) {

            collisionCount++;
            isColliding = true;

            System.out.println("Collision Count: " + collisionCount);
        }

    }

    public void update() {

        if (!playerCollidesWithObstacle()) {
            isColliding = false;
        }
    }


    private boolean checkCollisionWithObstacle() {
        Rectangle characterBounds = new Rectangle(100, characterY, character.getWidth(null), character.getHeight(null));

        int obstacleWidth = obstacle.getWidth(null) / 2;
        int obstacleHeight = obstacle.getHeight(null);
        Rectangle obstacleBounds = new Rectangle(obstacleX + (obstacle.getWidth(null) / 4), 430, obstacleWidth, obstacleHeight);

        return characterBounds.intersects(obstacleBounds);
    }

    private boolean checkCollisionWithTeddy() {
        Rectangle characterBounds = new Rectangle(100, characterY, character.getWidth(null), character.getHeight(null));
        Rectangle teddyBounds = new Rectangle(teddyX, 350, teddy.getWidth(null), teddy.getHeight(null));
        return characterBounds.intersects(teddyBounds);
    }

    @Override
    public void run() {
        while (true) {
            if (gameState == GameState.GAME && !gameOver) {
                updateGame();
                handleCollision();
                repaint();
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateGame() {

        bgX -= 5;
        if (bgX <= -getWidth()) {
            bgX = 0;
        }
        distanceScore += 1;

        if (jumping) {
            if (characterY > jumpHeight) {
                characterY -= 10;
            } else {
                Timer holdTimer = new Timer(500, event -> {
                    jumping = false;
                    ((Timer) event.getSource()).stop();
                });
                holdTimer.setRepeats(false);
                holdTimer.start();
            }
        } else if (characterY < groundLevel) {
            characterY += 10;  // Falling down
        }
        obstacleX -= 5;
        if (obstacleX < -50) {
            obstacleX = getWidth();
        }

        teddyX -= 5;
        if (teddyX < -64) {
            teddyX = getWidth() + 200;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (gameState) {
            case STORY1, STORY2, INSTRUCTIONS, GAME_OVER ->
                    paintScreens(g);
            case GAME ->
                    paintGame(g);

        }
    }

    private void paintScreens(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 21));

        switch (gameState) {
            case STORY1 -> {
                g.drawImage(nextStory1, 0, 0, getWidth(), getHeight(), this);
                g.drawString("I lost teddy whilst I was on holiday and I can't find him.", 70, 400);
            }
            case STORY2 -> {
                g.drawImage(nextStory2, 0, 0, getWidth(), getHeight(), this);
                g.drawString("I need your help! ...BUT be careful with your step.", 70, 400);
            }
            case INSTRUCTIONS -> {
                g.drawImage(startScreen, 0, 0, getWidth(), getHeight(), this);
            }
            case GAME_OVER -> {
                g.drawImage(tryAgain, 0, 0, getWidth(), getHeight(), this);

                g.setFont(new Font("Arial", Font.BOLD, 25));
                g.setColor(Color.BLACK);
                g.drawString("Game Over! Try Again.", 360, 280);

                // Display score
                g.setFont(new Font("Arial", Font.BOLD, 23));
                g.setColor(Color.BLACK);
                g.drawString("score: " + distanceScore, 400, 320);

                // Display teddy count
                g.drawImage(teddy, 380, 340, 32, 32, this);
                g.drawString(": " + pick, 420, 370);
            }
            default -> {
            }
        }
    }

    public void drawHeart(Graphics g) {
        int x = 20;
        int y = 10;
        int width = 35;
        int height = 35;

        if (myheart != null) {
            for (int i = 0; i < Math.min(heartCount, 3); i++) {

                g.drawImage(myheart, x + i * (width + 5), y, width, height, null);
            }
        } else {
            System.out.println("Heart image not loaded!");
        }
    }

    private void decreaseHeartCount() {
        if (heartCount > 0) {
            heartCount--;
            if (heartCount == 0) {

                gameState = GameState.GAME_OVER;
                gameOver = true;
                add(tryAgainButton);
                repaint();
            }
        }
    }

    private void handleCollision() {
        if (checkCollisionWithObstacle() && !hasCollided) {
            hasCollided = true;

            decreaseHeartCount();

            HitCount++;
            printCollisionMessage();
            printHeartCount();

            if (HitCount == maxCollisions || heartCount == 0) {
                gameState = GameState.GAME_OVER;
                gameOver = true;
                remove(backgroundButton1);
                remove(backgroundButton2);
                remove(backgroundButton3);
                add(tryAgainButton);
            }

            new Timer(1000, evt -> resetCollisionState()).start();
        }
        if (checkCollisionWithTeddy()) {
            pick++;
            teddyX = -100;
        }
    }

    private void resetCollisionState() {
        hasCollided = false;
    }

    private void paintGame(Graphics g) {
        Image currentBg = switch (currentBackground) {
            case 0 ->
                    background;
            case 1 ->
                    background2;
            case 2 ->
                    background3;
            default ->
                    background;
        };

        g.drawImage(currentBg, bgX, 0, getWidth(), getHeight(), this);
        g.drawImage(currentBg, bgX + getWidth(), 0, getWidth(), getHeight(), this);

        g.drawImage(character, 100, characterY - character.getHeight(null) + 30, character.getWidth(null) * 2, character.getHeight(null) * 2, this);
        g.drawImage(obstacle, obstacleX, 430, this);
        g.drawImage(teddy, teddyX, 350, this);

        drawHeart(g);

        if (hasCollided && HitCount <= maxCollisions) {
            g.drawImage(hitEffect, 105, 350, character.getWidth(null) * 2, character.getHeight(null) * 2, this);
        }

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);

        g.drawString("Score: " + distanceScore, 20, 70);

        g.drawImage(teddy, 15, 80, 32, 32, this);
        g.drawString(": " + pick, 60, 105);

        if (jumping) {
            g.setColor(Color.RED);
            g.fillRect(100 + (character.getWidth(this) / 2), characterY - jumpHeight + 20, 10, 10);
        }
    }

    private void printCollisionMessage() {
        System.out.println("Hitcount: " + HitCount);
    }

    private void printHeartCount() {
        System.out.println("heartcount: " + heartCount);
    }
} 