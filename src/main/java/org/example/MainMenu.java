package org.example;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 70;
    private static final int START_BUTTON_Y = 250;
    private static final int INSTRUCTIONS_BUTTON_Y = START_BUTTON_Y + 90;
    private static final int BUTTON_FONT_SIZE = 30;

    private static final Color BUTTON_COLOR = new Color(255, 180, 193);

    private static final String MUSIC_PATH = "/The_Victory_Lap.wav";

    public MainMenu() {
        this.setTitle("Main menu- Sugar Rush");
        this.setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);//פ[מבטל את הפס העליון של החלון

        Utils.initializeMusic(MUSIC_PATH);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        this.setContentPane(backgroundPanel);

        int buttonX = (Main.WINDOW_WIDTH - BUTTON_WIDTH) / 2;
        RoundedButton startButton = new RoundedButton("Start", 40);
        startButton.setFont(new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE));
        startButton.setBackground(BUTTON_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBounds(buttonX, START_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        startButton.addActionListener(e -> startGame(backgroundPanel));
        backgroundPanel.add(startButton);

        RoundedButton instructionButton = new RoundedButton("How to play", 40);
        instructionButton.setFont(new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE));
        instructionButton.setBackground(BUTTON_COLOR);
        instructionButton.setForeground(Color.WHITE);
        instructionButton.setFocusPainted(false);
        instructionButton.setBounds(buttonX, INSTRUCTIONS_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        backgroundPanel.add(instructionButton);

        JButton soundButton = Utils.createSoundButton();
        backgroundPanel.add(soundButton);

        this.revalidate();//רענון הסידור של המסך
        this.repaint();// 2 הפקודות הן רענון חדפ
        this.setVisible(true);
    }

    private void startGame(BackgroundPanel backgroundPanel) {
        this.dispose(); // סוגר את החלון של הבאקראונד
        JFrame window = new JFrame("Sugar Rush");
        window.setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        window.setUndecorated(true);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setLayout(null);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(new MainScenePanel(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT));
        window.setVisible(true);

    }
}