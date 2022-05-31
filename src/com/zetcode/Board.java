package com.zetcode;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BiPredicate;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Board extends JPanel {
    private final int OFFSET = 30;
    private final int SPACE = 20;
    private final int LEFT_COLLISION = 1;
    private final int RIGHT_COLLISION = 2;
    private final int TOP_COLLISION = 3;
    private final int BOTTOM_COLLISION = 4;
    private final int BOTTOM_P = 5;

    boolean VAR_TRUE = true;
    boolean VAR_FALSE = false;

    private ArrayList<Wall> walls;
    private ArrayList<Llaw> sllaw;
    private ArrayList<Baggage> baggs;
    private ArrayList<Area> areas;

    private Player soko;
    private int w = 0;
    private int h = 0;

    private boolean isCompleted = false;

    private String level;

    ConnectionBD bd = new ConnectionBD();
    String playerName = JOptionPane.showInputDialog(null, "Digite el nombre del jugador.", "Sokoban", JOptionPane.INFORMATION_MESSAGE);

    public Board() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        String fileName = JOptionPane.showInputDialog(null, "Digite el nombre del archivo.", "Sokoban", JOptionPane.INFORMATION_MESSAGE);

        menuMusicTheme();
        insertBoard(fileName);
        initBoard();
    }

    public void playerPoints(int points) {
        bd.createUser(playerName, points);
    }

    private void insertBoard(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("src/boards/" + file + ".txt"));
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
                level += line + "\n";
            }

            br.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void menuMusicTheme() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File file = new File("src/sounds/menuTheme.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();

        clip.open(audioStream);
        clip.start();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        initWorld();
    }

    public int getBoardWidth() {
        return this.w;
    }

    public int getBoardHeight() {
        return this.h;
    }

    private void initWorld() {
        sllaw = new ArrayList<>();
        walls = new ArrayList<>();
        baggs = new ArrayList<>();
        areas = new ArrayList<>();

        int x = OFFSET;
        int y = OFFSET;

        Llaw llaw;
        Wall wall;
        Baggage b;
        Area a;

        for (int i = 0; i < level.length(); i++) {
            char item = level.charAt(i);

            switch (item) {
                case '\n':
                    y += SPACE;

                    if (this.w < x) {
                        this.w = x;
                    }

                    x = OFFSET;
                    break;
                case '#':
                    wall = new Wall(x, y);
                    walls.add(wall);

                    x += SPACE;

                    break;
                case '/':
                    llaw = new Llaw(x, y);
                    sllaw.add(llaw);

                    x += SPACE;

                    break;
                case '$':
                    b = new Baggage(x, y);
                    baggs.add(b);

                    x += SPACE;

                    break;
                case '.':
                    a = new Area(x, y);
                    areas.add(a);

                    x += SPACE;

                    break;
                case '@':
                    soko = new Player(x, y);
                    x += SPACE;

                    break;
                case ' ':
                    x += SPACE;

                    break;
                default:
                    break;
            }
            h = y;
        }
    }

    private void buildWorld(Graphics g) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        ArrayList<Actor> world = new ArrayList<>();

        world.addAll(walls);
        world.addAll(sllaw);
        world.addAll(areas);
        world.addAll(baggs);
        world.add(soko);

        for (int i = 0; i < world.size(); i++) {
            Actor item = world.get(i);

            if (item instanceof Player || item instanceof Baggage) {
                g.drawImage(item.getImage(), item.x() + 2, item.y() + 2, this);
            } else {
                g.drawImage(item.getImage(), item.x(), item.y(), this);
            }

            if (isCompleted) {
                g.setColor(new Color(0, 0, 0));
                g.drawString("Completed", 25, 20);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        try {
            buildWorld(g);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (isCompleted) {
                return;
            }

            int key = e.getKeyCode();

            switch (key) {
                case KeyEvent.VK_LEFT:
                    if (checkWallCollision(soko, LEFT_COLLISION)) {
                        return;
                    }

                    if (checkLlawCollision(soko, LEFT_COLLISION)) {
                        return;
                    }

                    try {
                        if (checkBagCollision(LEFT_COLLISION)) {
                            return;
                        }
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e4) {
                        // TODO Auto-generated catch block
                        e4.printStackTrace();
                    }

                    soko.move(-SPACE, 0);

                    break;
                case KeyEvent.VK_RIGHT:
                    if (checkWallCollision(soko, RIGHT_COLLISION)) {
                        return;
                    }

                    if (checkLlawCollision(soko, RIGHT_COLLISION)) {
                        return;
                    }

                    try {
                        if (checkBagCollision(RIGHT_COLLISION)) {
                            return;
                        }
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e3) {
                        // TODO Auto-generated catch block
                        e3.printStackTrace();
                    }

                    soko.move(SPACE, 0);

                    break;
                case KeyEvent.VK_UP:
                    if (checkWallCollision(soko, TOP_COLLISION)) {
                        return;
                    }

                    if (checkLlawCollision(soko, TOP_COLLISION)) {
                        return;
                    }

                    try {
                        if (checkBagCollision(TOP_COLLISION)) {
                            return;
                        }
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }

                    soko.move(0, -SPACE);

                    break;
                case KeyEvent.VK_DOWN:
                    if (checkWallCollision(soko, BOTTOM_COLLISION)) {
                        return;
                    }

                    if (checkLlawCollision(soko, BOTTOM_COLLISION)) {
                        return;
                    }

                    try {
                        if (checkBagCollision(BOTTOM_COLLISION)) {
                            return;
                        }
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    soko.move(0, SPACE);

                    break;
                case KeyEvent.VK_R:
                    restartLevel();

                    break;
                case KeyEvent.VK_P:

                    VAR_FALSE = true;
                    VAR_TRUE = false;

                    break;
                case KeyEvent.VK_O:

                    VAR_FALSE = false;
                    VAR_TRUE = true;

                    break;
                default:
                    break;
            }
            repaint();
        }
    }

    private boolean checkWallCollision(Actor actor, int type) {
        switch (type) {
            case LEFT_COLLISION:
                for (int i = 0; i < walls.size(); i++) {
                    Wall wall = walls.get(i);

                    if (actor.isLeftCollision(wall)) {
                        return VAR_TRUE;
                    }
                }

                return VAR_FALSE;
            case RIGHT_COLLISION:
                for (int i = 0; i < walls.size(); i++) {

                    Wall wall = walls.get(i);

                    if (actor.isRightCollision(wall)) {
                        return VAR_TRUE;
                    }
                }

                return VAR_FALSE;
            case TOP_COLLISION:
                for (int i = 0; i < walls.size(); i++) {
                    Wall wall = walls.get(i);

                    if (actor.isTopCollision(wall)) {

                        return VAR_TRUE;
                    }
                }

                return VAR_FALSE;
            case BOTTOM_COLLISION:
                for (int i = 0; i < walls.size(); i++) {
                    Wall wall = walls.get(i);

                    if (actor.isBottomCollision(wall)) {

                        return VAR_TRUE;
                    }
                }

                return VAR_FALSE;
            default:
                break;
        }

        return VAR_FALSE;
    }

    private boolean checkLlawCollision(Actor actor, int type) {
        switch (type) {
            case LEFT_COLLISION:
                for (int i = 0; i < sllaw.size(); i++) {
                    Llaw llaw = sllaw.get(i);

                    if (actor.isLeftCollision(llaw)) {

                        return true;
                    }
                }

                return false;
            case RIGHT_COLLISION:
                for (int i = 0; i < sllaw.size(); i++) {

                    Llaw llaw = sllaw.get(i);

                    if (actor.isRightCollision(llaw)) {
                        return true;
                    }
                }

                return false;
            case TOP_COLLISION:
                for (int i = 0; i < sllaw.size(); i++) {

                    Llaw llaw = sllaw.get(i);

                    if (actor.isTopCollision(llaw)) {

                        return true;
                    }
                }

                return false;
            case BOTTOM_COLLISION:
                for (int i = 0; i < sllaw.size(); i++) {
                    Llaw llaw = sllaw.get(i);

                    if (actor.isBottomCollision(llaw)) {

                        return true;
                    }
                }

                return false;
            default:
                break;
        }

        return false;
    }

    private boolean checkBagCollision(int type) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        switch (type) {
            case LEFT_COLLISION:
                for (int i = 0; i < baggs.size(); i++) {
                    Baggage bag = baggs.get(i);

                    if (soko.isLeftCollision(bag)) {
                        for (int j = 0; j < baggs.size(); j++) {
                            Baggage item = baggs.get(j);

                            if (!bag.equals(item)) {
                                if (bag.isLeftCollision(item)) {
                                    return true;
                                }
                            }

                            if (checkWallCollision(bag, LEFT_COLLISION)) {
                                return true;
                            }
                        }

                        bag.move(-SPACE, 0);
                        isCompleted();
                    }
                }

                return false;
            case RIGHT_COLLISION:
                for (int i = 0; i < baggs.size(); i++) {
                    Baggage bag = baggs.get(i);

                    if (soko.isRightCollision(bag)) {
                        for (int j = 0; j < baggs.size(); j++) {

                            Baggage item = baggs.get(j);

                            if (!bag.equals(item)) {
                                if (bag.isRightCollision(item)) {
                                    return true;
                                }
                            }

                            if (checkWallCollision(bag, RIGHT_COLLISION)) {
                                return true;
                            }
                        }

                        bag.move(SPACE, 0);
                        isCompleted();
                    }
                }
                return false;
            case TOP_COLLISION:
                for (int i = 0; i < baggs.size(); i++) {
                    Baggage bag = baggs.get(i);

                    if (soko.isTopCollision(bag)) {
                        for (int j = 0; j < baggs.size(); j++) {
                            Baggage item = baggs.get(j);

                            if (!bag.equals(item)) {
                                if (bag.isTopCollision(item)) {
                                    return true;
                                }
                            }

                            if (checkWallCollision(bag, TOP_COLLISION)) {
                                return true;
                            }
                        }

                        bag.move(0, -SPACE);
                        isCompleted();
                    }
                }

                return false;
            case BOTTOM_COLLISION:
                for (int i = 0; i < baggs.size(); i++) {
                    Baggage bag = baggs.get(i);

                    if (soko.isBottomCollision(bag)) {
                        for (int j = 0; j < baggs.size(); j++) {
                            Baggage item = baggs.get(j);

                            if (!bag.equals(item)) {

                                if (bag.isBottomCollision(item)) {
                                    return true;
                                }
                            }

                            if (checkWallCollision(bag, BOTTOM_COLLISION)) {

                                return true;
                            }
                        }

                        bag.move(0, SPACE);
                        isCompleted();
                    }
                }

                break;
            default:
                break;
        }

        return false;
    }

    public void isCompleted() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        int nOfBags = baggs.size();
        int finishedBags = 0;

        for (int i = 0; i < nOfBags; i++) {
            Baggage bag = baggs.get(i);

            for (int j = 0; j < nOfBags; j++) {
                Area area = areas.get(j);

                if (bag.x() == area.x() && bag.y() == area.y()) {
                    finishedBags += 1;
                }
            }
        }

        if (finishedBags == nOfBags) {

            isCompleted = true;

            repaint();
            playerPoints(6);
            menuMusicTheme();
        }
    }

    private void restartLevel() {
        areas.clear();
        baggs.clear();
        walls.clear();
        sllaw.clear();

        initWorld();

        if (isCompleted) {
            isCompleted = false;
        }
    }

}
