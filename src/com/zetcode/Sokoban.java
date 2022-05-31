package com.zetcode;

import java.awt.EventQueue;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

public class Sokoban extends JFrame {
    private final int OFFSET = 30;

    public Sokoban() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        initUI();
    }

    private void initUI() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Board board = new Board();
        add(board);

        setTitle("Sokoban");
        
        setSize(board.getBoardWidth() + OFFSET,
                board.getBoardHeight() + 2 * OFFSET);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Sokoban game = null;
            
            try {
                game = new Sokoban();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            game.setVisible(true);
        });
    }
}
