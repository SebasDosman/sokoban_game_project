package com.zetcode;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Llaw extends Actor {

    private Image image;

    public Llaw(int x, int y) {
        super(x, y);
        
        initWall();
    }
    
    private void initWall() {
        
        ImageIcon iicon = new ImageIcon("src/resources/wall.png");
        image = iicon.getImage();
        setImage(image);
    }
}
